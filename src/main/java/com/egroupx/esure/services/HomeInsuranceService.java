package com.egroupx.esure.services;

import com.egroupx.esure.model.AllRisks;
import com.egroupx.esure.model.Building;
import com.egroupx.esure.model.HouseHoldContents;
import com.egroupx.esure.model.SHLink;
import com.egroupx.esure.repository.HomeInsuranceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;

@Service
public class HomeInsuranceService {


    @Value("${egroupx.services.fspEndpointUrl}")
    private String fspEndpointUrl;

    @Value("${egroupx.services.fspAPIKey:}")
    private String fspAPIKey;

    private WebClient webClient;

    private final Logger LOG = LoggerFactory.getLogger(HomeInsuranceService.class);

    private final HomeInsuranceRepository homeInsuranceRepository;

    public HomeInsuranceService(HomeInsuranceRepository homeInsuranceRepository) {
        this.homeInsuranceRepository = homeInsuranceRepository;
    }

    Mono<String> saveBuildingDetails(Long fspQouteRefId, String description, String coverType, String construction, String roofConstruction, String sumInsured, String recentLossCount, String propertyOwnedclaim){
        return homeInsuranceRepository.saveBuildingDetails(fspQouteRefId,description,coverType,construction,roofConstruction,sumInsured,recentLossCount,propertyOwnedclaim).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed saving building details {0}",fspQouteRefId));
            return Mono.just("Completed saving building details "+ fspQouteRefId);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save building details {0}",err.getMessage()));
            return Mono.just("Failed to save building details");
        });
    }

    Mono<String> saveBuildingShLinkDetails(Long fspQouteRefId, String linkTypeId, Long fspShLinkRefId){
        return homeInsuranceRepository.saveBuildingShLinkDetails(fspQouteRefId,linkTypeId,fspShLinkRefId).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed building sh link details {0}",fspQouteRefId));
            return Mono.just("Completed saving building sh link details "+ fspQouteRefId);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save building sh link details {0}",err.getMessage()));
            return Mono.just("Failed to save building sh link details");
        });
    }

    Mono<String> saveHouseHoldContentsDetails(Long fspQouteRefId, String description, String sumInsured, String restrictedCover, String unoccPeriod, String unrelatedCount, String standardWalls, String thatchedRoof, String burglarBars, String securityGates, String alarmInWorkingOrder, String recentLossCount, String propertyOwnedClaim){
        return homeInsuranceRepository.saveHouseHoldContentsDetails(fspQouteRefId,description,sumInsured,restrictedCover,unoccPeriod,unrelatedCount,standardWalls,thatchedRoof,burglarBars,securityGates,alarmInWorkingOrder,recentLossCount,propertyOwnedClaim).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed saving house hold contents details {0}",fspQouteRefId));
            return Mono.just("Completed saving house hold contents details "+ fspQouteRefId);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save house hold contents details {0}",err.getMessage()));
            return Mono.just("Failed to save house hold contents details");
        });
    }

    Mono<String> saveHouseHoldShLinkDetails(Long fspQouteRefId,String linkTypeId,Long fspShLinkRefId){
        return homeInsuranceRepository.saveHouseHoldShLinkDetails(fspQouteRefId,linkTypeId,fspShLinkRefId).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed saving house hold sh link details {0}",fspQouteRefId));
            return Mono.just("Completed saving house hold sh link details "+ fspQouteRefId);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save house hold sh link details {0}",err.getMessage()));
            return Mono.just("Failed to save house hold sh link details");
        });
    }

    Flux<Building> getBuildingDetails(Long qouteRefId) {
        return homeInsuranceRepository.getBuildingByQuoteRefId(qouteRefId).flatMap(building -> {
            LOG.info(MessageFormat.format("Successfully retrieved all building detail {0}",qouteRefId));
            return Mono.just(building);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to retrieve all building by id {0}. Error {1}",qouteRefId, err.getMessage()));
            return Mono.empty();
        });
    }

    Flux<SHLink> getBuildingShLinkDetails(Long qouteRefId) {
        return homeInsuranceRepository.getBuildingShLinkByQuoteRefId(qouteRefId).flatMap(shLink -> {
            LOG.info(MessageFormat.format("Successfully retrieved building sh link detail {0}",qouteRefId));
            return Mono.just(shLink);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to retrieve building sh link by id {0}. Error {1}",qouteRefId, err.getMessage()));
            return Mono.empty();
        });
    }

    Flux<HouseHoldContents> getHouseHoldContentsDetails(Long qouteRefId) {
        return homeInsuranceRepository.getHouseHoldContentsByQuoteRefId(qouteRefId).flatMap(houseHoldContents -> {
            LOG.info(MessageFormat.format("Successfully retrieved household contents detail {0}",qouteRefId));
            return Mono.just(houseHoldContents);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to retrieve household contents detail by id {0}. Error {1}",qouteRefId, err.getMessage()));
            return Mono.empty();
        });
    }

    Flux<SHLink> getHouseHoldContentsShLink(Long qouteRefId) {
        return homeInsuranceRepository.getHouseHoldShLinkByQuoteRefId(qouteRefId).flatMap(shLink -> {
            LOG.info(MessageFormat.format("Successfully retrieved household sh link detail {0}",qouteRefId));
            return Mono.just(shLink);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to retrieve  household sh link  detail by id {0}. Error {1}",qouteRefId, err.getMessage()));
            return Mono.empty();
        });
    }

}
