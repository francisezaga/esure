package com.egroupx.esure.services;

import com.egroupx.esure.model.Suburb;
import com.egroupx.esure.model.responses.api.APIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Base64;

@Service
public class CustomerService {

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

    public Mono<ResponseEntity<APIResponse>> getAddressSuburbs(String suburbName) {
        setConfigs(fspEndpointUrl);
        return webClient.get()
                .uri("/api/insure/lookups/suburbs?filter="+suburbName)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, "*/*")
                .retrieve()
                .bodyToMono(Suburb[].class).map(suburbs -> {
                    LOG.info("Retrieved address suburbs");
                    return ResponseEntity.ok().body(new APIResponse(200, "success",suburbs, Instant.now()));
                }).onErrorResume(error -> {
                    LOG.info(MessageFormat.format("Failed to retrieve address suburbs list {0}", error.getMessage()));
                    return Mono.just(ResponseEntity.internalServerError().body(new APIResponse(500, "Failed", "Failed to retrieve vehicle list", Instant.now())));
                });
    }
}
