package com.egroupx.esure.services;

import com.egroupx.esure.dto.fsp_policy.Policy;
import com.egroupx.esure.model.responses.api.APIResponse;

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
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Base64;

@Service
public class PolicyService {

    @Value("${egroupx.services.fspEndpointUrl}")
    private String fspEndpointUrl;

    @Value("${egroupx.services.fspAPIKey:}")
    private String fspAPIKey;

    private WebClient webClient;

    private final Logger LOG = LoggerFactory.getLogger(PolicyService.class);

    private void setConfigs(String endpointUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(endpointUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " +  Base64.getEncoder().encodeToString((fspAPIKey+":"+"").getBytes()))
                .build();
    }

    public Mono<ResponseEntity<APIResponse>> acceptPolicy(Long policyId, Policy policyReq) {

        setConfigs(fspEndpointUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        String formattedPolicyReq = null;
        try {
            formattedPolicyReq = objectMapper.writeValueAsString(policyReq);
        }catch(JsonProcessingException ex){
            return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400, "Failed", "Failed to get process request", Instant.now())));
        }
        return webClient.put()
                .uri("/api/insure/policies/"+policyId+"/accept")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, "*/*")
                .body(BodyInserters.fromObject(formattedPolicyReq))
                .retrieve()
                .toEntity(Object.class).map(responseEntity -> {
                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                        Object obj2 = responseEntity.getBody();
                        LOG.info("Successfully processed policy");
                        return ResponseEntity.ok().body(new APIResponse(200, "success",obj2, Instant.now()));
                    } else {
                        LOG.error(MessageFormat.format("Failed to process policy. Error code {0}", responseEntity.getStatusCode().value()));
                        return ResponseEntity.badRequest().body(new APIResponse(responseEntity.getStatusCode().value(), "Failed to process policy","Failed to process policy. Please try again or contact admin", Instant.now()));
                    }
                }).onErrorResume(error -> {
                    LOG.error(MessageFormat.format("Failed to process policy. Error code {0}", error.getMessage()));
                    return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400, "Failed","Failed to process policy. Please try again or contact admin", Instant.now())));
                });
    }
}
