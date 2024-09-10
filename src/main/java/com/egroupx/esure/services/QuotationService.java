package com.egroupx.esure.services;

import com.egroupx.esure.model.Car;
import com.egroupx.esure.model.Quotation;

import com.egroupx.esure.model.responses.APIResponse;
import com.egroupx.esure.model.responses.CalculationsResponse;
import com.egroupx.esure.model.responses.QuotationResponse;
import com.egroupx.esure.util.AppUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Base64;

@Service
public class QuotationService {

    @Value("${egroupx.services.fspEndpointUrl}")
    private String fspEndpointUrl;

    @Value("${egroupx.services.fspAPIKey:}")
    private String fspAPIKey;

    private WebClient webClient;

    private final Logger LOG = LoggerFactory.getLogger(CustomerService.class);

    private void setConfigs(String endpointUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(endpointUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " +  Base64.getEncoder().encodeToString((fspAPIKey+":"+"").getBytes()))
                .build();
    }

    public Mono<ResponseEntity<APIResponse>> requestQuotation(Quotation quotation) {

        setConfigs(fspEndpointUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        String formattedQuotationReq = null;
        try {
            formattedQuotationReq = objectMapper.writeValueAsString(quotation);
        }catch(JsonProcessingException ex){
            return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400, "Failed", "Failed to get process request", Instant.now())));
        }

        return webClient.post()
                .uri("/api/insure/quotations")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, "*/*")
                .body(BodyInserters.fromObject(formattedQuotationReq))
                .retrieve()
                .toEntity(QuotationResponse.class).map(responseEntity -> {
                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                        Object resObj = responseEntity.getBody();
                        QuotationResponse quotationRes = null;
                        LOG.info("Successfully received quotation");
                        try {
                            String formattedQuotationRes = objectMapper.writeValueAsString(resObj);
                            quotationRes = objectMapper.readValue(formattedQuotationRes, QuotationResponse.class);
                            LOG.info("Successfully deserialized quotation response");
                            return ResponseEntity.ok().body(new APIResponse(200, "success",quotationRes, Instant.now()));
                        }catch(JsonProcessingException ex){
                            return ResponseEntity.ok().body(new APIResponse(200, "success",resObj, Instant.now()));
                        }
                    } else {
                        LOG.info(MessageFormat.format("Failed to issue quotation. Error code {0}", responseEntity.getStatusCode().value()));
                        return ResponseEntity.badRequest().body(new APIResponse(responseEntity.getStatusCode().value(), "Failed to issue quotation.", responseEntity.getStatusCode().value(), Instant.now()));
                    }
                }).onErrorResume(error -> {
                    LOG.info(MessageFormat.format("Failed to to get quotation {0}", error.getMessage()));
                    return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400, "Failed", "Failed to get quotation", Instant.now())));
                });
    }

    public Mono<ResponseEntity<APIResponse>> calculateQuotation(Long quotationId) {
        setConfigs(fspEndpointUrl);
        return webClient.post()
                .uri("/api/insure/calculations/calculate/quotations/"+quotationId)
                .header(HttpHeaders.ACCEPT, "*/*")
                .retrieve()
                .toEntity(CalculationsResponse.class).map(responseEntity -> {
                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                        Object resObj = responseEntity.getBody();

                        LOG.info("Successfully triggered calculation of quotation");
                        return ResponseEntity.ok().body(new APIResponse(200, "success",resObj, Instant.now()));
                    } else {
                        LOG.info(MessageFormat.format("Failed to trigger calculation of quotation. Error code {0}", responseEntity.getStatusCode().value()));
                        return ResponseEntity.badRequest().body(new APIResponse(responseEntity.getStatusCode().value(), "Failed to trigger calculation of quotation.", responseEntity.getStatusCode().value(), Instant.now()));
                    }
                }).onErrorResume(error -> {
                    LOG.info(MessageFormat.format("Failed to trigger calculation of quotation {0}", error.getMessage()));
                    return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400, "Failed", "Failed to trigger calculation of quotation", Instant.now())));
                });
    }

    public Mono<ResponseEntity<APIResponse>> getQuotationResult(Long quotationId) {
        setConfigs(fspEndpointUrl);
        return webClient.get()
                .uri("/api/insure/calculations/status/quotations/"+quotationId)
                .header(HttpHeaders.ACCEPT, "*/*")
                .retrieve()
                .toEntity(Object.class).map(responseEntity -> {
                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                        Object resObj = responseEntity.getBody();

                        LOG.info("Successfully retrieved quotation result");
                        return ResponseEntity.ok().body(new APIResponse(200, "success",resObj, Instant.now()));
                    } else {
                        LOG.info(MessageFormat.format("Failed to retrieve quotation result. Error code {0}", responseEntity.getStatusCode().value()));
                        return ResponseEntity.badRequest().body(new APIResponse(responseEntity.getStatusCode().value(), "Failed to retrieve quotation result.", responseEntity.getStatusCode().value(), Instant.now()));
                    }
                }).onErrorResume(error -> {
                    LOG.info(MessageFormat.format("Failed to retrieve quotation result {0}", error.getMessage()));
                    return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400, "Failed", "Failed to retrieve quotation result", Instant.now())));
                });
    }
}
