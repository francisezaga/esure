package com.egroupx.esure.services;

import com.egroupx.esure.model.MotorVehicleShortTerm;
import com.egroupx.esure.model.MotorVehicles;
import com.egroupx.esure.model.SHLink;
import com.egroupx.esure.model.responses.api.APIResponse;
import com.egroupx.esure.model.VehicleManufacturer;
import com.egroupx.esure.repository.VehicleInsuranceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
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
                    LOG.info(MessageFormat.format("Failed to retrieve vehicle manufacturer list {0}", error.getMessage()));
                    return Mono.just(ResponseEntity.internalServerError().body(new APIResponse(500, "Failed", "Failed to retrieve vehicle manufacturers", Instant.now())));
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
                    LOG.info(MessageFormat.format("Failed to retrieve vehicle model list by year and manufacturer code {0}", error.getMessage()));
                    return Mono.just(ResponseEntity.internalServerError().body(new APIResponse(500, "Failed", "Failed to retrieve vehicle models", Instant.now())));
                });
    }

    Mono<String> saveVehicleDetails(Long fspQouteRefId, String year, String make, String model, String carColor, String metallicPaint, String quotationBasis, String alarmTypeId, String alarmByVesa, String tracingDevice){
        return vehicleInsuranceRepository.saveVehicleDetails(fspQouteRefId, year, make, model, carColor, metallicPaint,quotationBasis,alarmTypeId,alarmByVesa,tracingDevice).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed saving vehicle details {0}",fspQouteRefId));
            return Mono.just("Completed saving vehicle details "+ fspQouteRefId);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to vehicle details {0}",err.getMessage()));
            return Mono.just("Failed to save vehicle details");
        });
    }

    Mono<String> saveVehicleShLinkDetails(Long fspQouteRefId, String linkTypeId, Long fspShLinkRefId){
        return vehicleInsuranceRepository.saveVehicleShLinkDetails(fspQouteRefId,linkTypeId,fspShLinkRefId).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed saving vehicle sh link details {0}",fspQouteRefId));
            return Mono.just("Completed saving vehicle sh link details "+ fspQouteRefId);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save vehicle sh link details {0}",err.getMessage()));
            return Mono.just("Failed to save vehicle sh link details");
        });
    }

    Mono<String> saveVehicleShortTermDetails(Long fspQouteRefId, String coverType, String useTypeId, String flatExcess, String overnightParkingCd, String overnightParkingTypeLocked){
        return vehicleInsuranceRepository.saveVehicleShortTermDetails(fspQouteRefId,coverType,useTypeId,flatExcess,overnightParkingCd,overnightParkingTypeLocked).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed saving vehicle short term details {0}",fspQouteRefId));
            return Mono.just("Completed saving vehicle short term details "+ fspQouteRefId);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save vehicle short term details {0}",err.getMessage()));
            return Mono.just("Failed to save vehicle short term details");
        });
    }

    Flux<MotorVehicles> getMotorVehicleByQuoteRefId(Long qouteRefId) {
        return vehicleInsuranceRepository.getMotorVehiclesByQuoteRefId(qouteRefId).flatMap(shLink -> {
            LOG.info(MessageFormat.format("Successfully retrieved vehicle  detail {0}",qouteRefId));
            return Mono.just(shLink);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to retrieve vehicle by id {0}. Error {1}",qouteRefId, err.getMessage()));
            return Mono.empty();
        });
    }

    Flux<SHLink> getMotorVehicleShLink(Long qouteRefId) {
        return vehicleInsuranceRepository.getMotorVehicleLinkByQuoteRefId(qouteRefId).flatMap(shLink -> {
            LOG.info(MessageFormat.format("Successfully retrieved vehicle sh link detail {0}",qouteRefId));
            return Mono.just(shLink);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to retrieve vehicle sh link  detail by id {0}. Error {1}",qouteRefId, err.getMessage()));
            return Mono.empty();
        });
    }

    Mono<MotorVehicleShortTerm> getMotorVehicleShortTerm(Long qouteRefId) {
        return vehicleInsuranceRepository.getMotorVehicleShortTermByQuoteRefId(qouteRefId).flatMap(shLink -> {
            LOG.info(MessageFormat.format("Successfully retrieved vehicle short term detail {0}",qouteRefId));
            return Mono.just(shLink);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to retrieve vehicle short term detail by id {0}. Error {1}",qouteRefId, err.getMessage()));
            return Mono.empty();
        });
    }

}
