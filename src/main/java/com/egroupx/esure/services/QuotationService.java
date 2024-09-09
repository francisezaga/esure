package com.egroupx.esure.services;

import com.egroupx.esure.model.Car;
import com.egroupx.esure.model.Quotation;
import com.egroupx.esure.model.Suburb;
import com.egroupx.esure.model.responses.APIResponse;
import com.egroupx.esure.model.responses.QuotationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Instant;

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
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE,
                        "Content-Type", MediaType.APPLICATION_JSON_VALUE
                ).build();
    }

    public Mono<ResponseEntity<APIResponse>> requestQuotation(Quotation quotation) {
        setConfigs(fspEndpointUrl);
        return webClient.post()
                .uri("/api/insure/quotations")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, "*/*")
                .bodyValue(quotation)
                .retrieve()
                .bodyToMono(QuotationResponse.class).map(quotationResponse -> {
                    LOG.info("Received quotation");
                    return ResponseEntity.ok().body(new APIResponse(200, "success",quotationResponse, Instant.now()));
                }).onErrorResume(error -> {
                    LOG.info(MessageFormat.format("Failed to to get quotation {0}", error.getMessage()));
                    return Mono.just(ResponseEntity.internalServerError().body(new APIResponse(500, "Failed", "Failed to retrieve vehicle list", Instant.now())));
                });
    }

    public Flux<Car> calculateQuotation(Long quotationId) {
        setConfigs("/api/insure/calculations/calculate/quotations/"+quotationId);
        return Flux.empty();
    }

    public Flux<Car> getQuotationResult(Long quotationId) {
        setConfigs("/api/insure/calculations/status/quotations/"+quotationId);
        return Flux.empty();
    }
}
