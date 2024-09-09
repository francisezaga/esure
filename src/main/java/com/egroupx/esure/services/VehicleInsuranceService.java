package com.egroupx.esure.services;

import com.egroupx.esure.model.responses.APIResponse;
import com.egroupx.esure.model.VehicleManufacturer;
import com.egroupx.esure.repository.VehicleInsuranceRepository;
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
public class VehicleInsuranceService {

    @Value("${egroupx.services.fspEndpointUrl}")
    private String fspEndpointUrl;

    @Value("${egroupx.services.fspAPIKey:}")
    private String fspAPIKey;

    private WebClient webClient;

    private final VehicleInsuranceRepository vehicleInsuranceRepository;

    private final Logger LOG = LoggerFactory.getLogger(VehicleInsuranceService.class);

    public VehicleInsuranceService(VehicleInsuranceRepository vehicleInsuranceRepository) {
        this.vehicleInsuranceRepository = vehicleInsuranceRepository;
    }

    private void setConfigs(String endpointUrl) {

        this.webClient = WebClient.builder()
                .baseUrl(endpointUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " +  Base64.getEncoder().encodeToString((fspAPIKey+":"+"").getBytes()))
                .build();
    }

    public Mono<ResponseEntity<APIResponse>> getVehicleManufacturersByYear(int year) {
        setConfigs(fspEndpointUrl);
        return webClient.get()
                .uri("/api/insure/lookups/vehicles/manufacturers?year="+year)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, "*/*")
                .retrieve()
                .bodyToMono(VehicleManufacturer[].class).map(vehicleManufacturers -> {
                        LOG.info("Retrieved vehicle list by year");
                       return ResponseEntity.ok().body(new APIResponse(200, "success",vehicleManufacturers, Instant.now()));
                }).onErrorResume(error -> {
                    LOG.info(MessageFormat.format("Failed to retrieve vehicle list {0}", error.getMessage()));
                    return Mono.just(ResponseEntity.internalServerError().body(new APIResponse(500, "Failed", "Failed to retrieve vehicle list", Instant.now())));
                });
    }

    public Mono<ResponseEntity<APIResponse>> getVehicleModelsByManufacturerCodeAndYear(String manufacturerCode,int year) {
        setConfigs(fspEndpointUrl);
        return webClient.get()
                .uri("/api/insure/lookups/vehicles/manufacturers/"+manufacturerCode+"/models?year="+year)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, "*/*")
                .retrieve()
                .bodyToMono(VehicleManufacturer[].class).map(vehicleManufacturers -> {
                    LOG.info("Retrieved vehicle list by year and manufacturer code");
                    return ResponseEntity.ok().body(new APIResponse(200, "success",vehicleManufacturers, Instant.now()));
                }).onErrorResume(error -> {
                    LOG.info(MessageFormat.format("Failed to retrieve vehicle list by year and manufacturer code {0}", error.getMessage()));
                    return Mono.just(ResponseEntity.internalServerError().body(new APIResponse(500, "Failed", "Failed to retrieve vehicle list by year and manufacturer code", Instant.now())));
                });
    }
}
