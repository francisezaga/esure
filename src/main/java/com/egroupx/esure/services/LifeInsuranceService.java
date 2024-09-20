package com.egroupx.esure.services;

import com.egroupx.esure.dto.v360.ProductDTO;
import com.egroupx.esure.model.life.*;
import com.egroupx.esure.model.responses.api.APIResponse;
import com.egroupx.esure.model.responses.life.LifeAPIResponse;
import com.egroupx.esure.model.responses.life.ProductsResponse;
import com.egroupx.esure.repository.LifeInsuranceRepository;
import com.egroupx.esure.util.AppUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Base64;

@Service
public class LifeInsuranceService {

    @Value("${egroupx.services.fspAPIKey}")
    private String fspAPIKey;

    @Value("${egroupx.services.pol360EndpointUrl}")
    private String pol360EndpointUrl;

    @Value("${egroupx.services.pol360UserName}")
    private String pol360UserName;

    @Value("${egroupx.services.pol360Password}")
    private String pol360Password;

    private WebClient webClient;

    private final TokenService tokenService;

    private final LifeInsuranceRepository lifeInsuranceRepository;

    private final Logger LOG = LoggerFactory.getLogger(LifeInsuranceService.class);

    public LifeInsuranceService(TokenService tokenService, LifeInsuranceRepository lifeInsuranceRepository) {
        this.tokenService = tokenService;
        this.lifeInsuranceRepository = lifeInsuranceRepository;
    }

    private void setConfigs(String endpointUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(endpointUrl)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE,
                        "Content-Type", MediaType.APPLICATION_JSON_VALUE
                ).build();
    }


    public Mono<ResponseEntity<APIResponse>> createMember(Member memberDTO) {
        setConfigs(pol360EndpointUrl);

        return tokenService.getPol360APIToken().flatMap(
                bearerToken -> {
                    if (!bearerToken.isBlank() || !bearerToken.isEmpty()) {

                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.registerModule(new JavaTimeModule());
                        String formattedReq = null;
                        try {
                            formattedReq = objectMapper.writeValueAsString(memberDTO);
                        } catch (JsonProcessingException ex) {
                            return Mono.just(new APIResponse(400, "Failed", "Failed to get process request", Instant.now()));
                        }

                        return webClient.post()
                                .uri("/api/360APITEST.php/api/360API.php")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.ACCEPT, "*/*")
                                 .header("Authorization", bearerToken)
                                .body(BodyInserters.fromObject(formattedReq))
                                .retrieve()
                                .toEntity(LifeAPIResponse.class).map(responseEntity -> {
                                   if (responseEntity.getStatusCode().is2xxSuccessful()) {
                                        LifeAPIResponse lifeAPIResponse = responseEntity.getBody();
                                        if (lifeAPIResponse != null && lifeAPIResponse.getResult()!=null && !lifeAPIResponse.getResult().toLowerCase().contains("err")) {
                                            String memberId = lifeAPIResponse != null ? String.valueOf(lifeAPIResponse.getMemberID()) : "";
                                            LOG.error(MessageFormat.format("Member successfully created member id{0}", memberId));
                                            return new APIResponse(200, "success", lifeAPIResponse, Instant.now());
                                        } else {
                                            LOG.error(MessageFormat.format("Failed to create member. Response status {0}", lifeAPIResponse.getMessage()));
                                            return new APIResponse(400, "fail", "Failed to create member. Response " + lifeAPIResponse.getMessage(), Instant.now());
                                        }
                                    } else {
                                        LOG.error(MessageFormat.format("Failed to create member. Response status {0}", responseEntity.getStatusCode().value()));
                                        return new APIResponse(400, "fail", "Failed to create member. Response code " + responseEntity.getStatusCode().value(), Instant.now());
                                    }
                                }).onErrorResume(error -> {
                                    LOG.error(MessageFormat.format("Failed to create member {0}", error.getMessage()));
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create member", Instant.now()));
                                });
                    } else {
                        return Mono.just(new APIResponse(400, "fail", "Error creating member.", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if (apiResponse.getStatus() == 200) {
                LifeAPIResponse lifeAPIResponse = AppUtil.dtoToEntity(apiResponse.getData(),new LifeAPIResponse());
                return saveMember(lifeAPIResponse.getPolicyNumber(), memberDTO.getClient(), memberDTO.getAgentCode(), memberDTO.getPolicyNumber(), memberDTO.getBrokerCode(), memberDTO.getTitle(), memberDTO.getFirstName(), memberDTO.getSurname(), memberDTO.getIdNumber(), memberDTO.getGender(), AppUtil.formatDate(memberDTO.getDateOfBirth()),
                        memberDTO.getAge(), memberDTO.getCellNumber(), memberDTO.getAltCellNumber(), memberDTO.getWorkNumber(), memberDTO.getHomeNumber(), memberDTO.getEmail(), memberDTO.getFax(), memberDTO.getContactType(), memberDTO.getPostalAddress1(), memberDTO.getPostalAddress2(), memberDTO.getPostalAddress3(), memberDTO.getPostalCode(), memberDTO.getResidentialAddress1(),
                        memberDTO.getResidentialAddress2(), memberDTO.getResidentialAddress3(), memberDTO.getResidentialCode(), memberDTO.getMemberType(), memberDTO.getPremium(), memberDTO.getCover(), memberDTO.getAddPolicyID(), memberDTO.getStatusCode())
                        .then(Mono.just("next"))
                        .flatMap(msg->{
                            return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
                        });
            } else {
               /* return saveMember("", memberDTO.getClient(), memberDTO.getAgentCode(), memberDTO.getPolicyNumber(), memberDTO.getBrokerCode(), memberDTO.getTitle(), memberDTO.getFirstName(), memberDTO.getSurname(), memberDTO.getIdNumber(), memberDTO.getGender(), memberDTO.getDateOfBirth(),
                        memberDTO.getAge(), memberDTO.getCellNumber(), memberDTO.getAltCellNumber(), memberDTO.getWorkNumber(), memberDTO.getHomeNumber(), memberDTO.getEmail(), memberDTO.getFax(), memberDTO.getContactType(), memberDTO.getPostalAddress1(), memberDTO.getPostalAddress2(), memberDTO.getPostalAddress3(), memberDTO.getPostalCode(), memberDTO.getResidentialAddress1(),
                        memberDTO.getResidentialAddress2(), memberDTO.getResidentialAddress3(), memberDTO.getResidentialCode(), memberDTO.getMemberType(), memberDTO.getPremium(), memberDTO.getCover(), memberDTO.getAddPolicyID(), memberDTO.getStatusCode());
                */
                return Mono.just(ResponseEntity.badRequest().body(apiResponse));
            }
        });
    }
    public Mono<ResponseEntity<APIResponse>> createBeneficiary(Beneficiary beneficiaryDTO) {
        setConfigs(pol360EndpointUrl);

        return tokenService.getPol360APIToken().flatMap(
                bearerToken -> {
                    if (!bearerToken.isBlank() || !bearerToken.isEmpty()) {

                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.registerModule(new JavaTimeModule());
                        String formattedReq = null;
                        try {
                            formattedReq = objectMapper.writeValueAsString(beneficiaryDTO);
                        } catch (JsonProcessingException ex) {
                            return Mono.just(new APIResponse(400, "Failed", "Failed to get process request", Instant.now()));
                        }

                        return webClient.post()
                                .uri("/api/360API.php")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.ACCEPT, "*/*")
                                .header("Authorization", bearerToken)
                                .body(BodyInserters.fromObject(formattedReq))
                                .retrieve()
                                .toEntity(LifeAPIResponse.class).map(responseEntity -> {
                                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                                        LifeAPIResponse lifeAPIResponse = responseEntity.getBody();
                                        if (lifeAPIResponse != null && lifeAPIResponse.getResult()!=null && !lifeAPIResponse.getResult().toLowerCase().contains("err")) {
                                            String memberId = lifeAPIResponse != null ? String.valueOf(lifeAPIResponse.getMemberID()) : "";
                                            LOG.error(MessageFormat.format("Member successfully created member id{0}", memberId));
                                            return new APIResponse(200, "success", lifeAPIResponse, Instant.now());
                                        } else {
                                            LOG.error(MessageFormat.format("Failed to create member. Response status {0}", lifeAPIResponse.getMessage()));
                                            return new APIResponse(400, "fail", "Failed to create member. Response " + lifeAPIResponse.getMessage(), Instant.now());
                                        }
                                    } else {
                                        LOG.error(MessageFormat.format("Failed to create member. Response status {0}", responseEntity.getStatusCode().value()));
                                        return new APIResponse(400, "fail", "Failed to create member. Response code " + responseEntity.getStatusCode().value(), Instant.now());
                                    }
                                }).onErrorResume(error -> {
                                    LOG.error(MessageFormat.format("Failed to create member {0}", error.getMessage()));
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create member", Instant.now()));
                                });
                    } else {
                        return Mono.just(new APIResponse(400, "fail", "Error creating member.", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if (apiResponse.getStatus() == 200) {
                LifeAPIResponse lifeAPIResponse = AppUtil.dtoToEntity(apiResponse.getData(),new LifeAPIResponse());
                /*return saveMember(lifeAPIResponse.getPolicyNumber(), memberDTO.getClient(), memberDTO.getAgentCode(), memberDTO.getPolicyNumber(), memberDTO.getBrokerCode(), memberDTO.getTitle(), memberDTO.getFirstName(), memberDTO.getSurname(), memberDTO.getIdNumber(), memberDTO.getGender(), AppUtil.formatDate(memberDTO.getDateOfBirth()),
                        memberDTO.getAge(), memberDTO.getCellNumber(), memberDTO.getAltCellNumber(), memberDTO.getWorkNumber(), memberDTO.getHomeNumber(), memberDTO.getEmail(), memberDTO.getFax(), memberDTO.getContactType(), memberDTO.getPostalAddress1(), memberDTO.getPostalAddress2(), memberDTO.getPostalAddress3(), memberDTO.getPostalCode(), memberDTO.getResidentialAddress1(),
                        memberDTO.getResidentialAddress2(), memberDTO.getResidentialAddress3(), memberDTO.getResidentialCode(), memberDTO.getMemberType(), memberDTO.getPremium(), memberDTO.getCover(), memberDTO.getAddPolicyID(), memberDTO.getStatusCode())
                        .then(Mono.just("next"))
                        .flatMap(msg->{
                            return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
                        });*/
                return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
            } else {
                return Mono.just(ResponseEntity.badRequest().body(apiResponse));
            }
        });
    }

    public Mono<ResponseEntity<APIResponse>> createDependent(Dependent dependentDTO) {
        setConfigs(pol360EndpointUrl);

        return tokenService.getPol360APIToken().flatMap(
                bearerToken -> {
                    if (!bearerToken.isBlank() || !bearerToken.isEmpty()) {

                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.registerModule(new JavaTimeModule());
                        String formattedReq = null;
                        try {
                            formattedReq = objectMapper.writeValueAsString(dependentDTO);
                        } catch (JsonProcessingException ex) {
                            return Mono.just(new APIResponse(400, "Failed", "Failed to get process request", Instant.now()));
                        }

                        return webClient.post()
                                .uri("/api/360API.php")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.ACCEPT, "*/*")
                                .header("Authorization", bearerToken)
                                .body(BodyInserters.fromObject(formattedReq))
                                .retrieve()
                                .toEntity(LifeAPIResponse.class).map(responseEntity -> {
                                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                                        LifeAPIResponse lifeAPIResponse = responseEntity.getBody();
                                        if (lifeAPIResponse != null && lifeAPIResponse.getResult()!=null && !lifeAPIResponse.getResult().toLowerCase().contains("err")) {
                                            String memberId = lifeAPIResponse != null ? String.valueOf(lifeAPIResponse.getMemberID()) : "";
                                            LOG.error(MessageFormat.format("Dependent successfully created id{0}", memberId));
                                            return new APIResponse(200, "success", lifeAPIResponse, Instant.now());
                                        } else {
                                            LOG.error(MessageFormat.format("Failed to create dependent. Response status {0}", lifeAPIResponse.getMessage()));
                                            return new APIResponse(400, "fail", "Failed to create dependent. Response " + lifeAPIResponse.getMessage(), Instant.now());
                                        }
                                    } else {
                                        LOG.error(MessageFormat.format("Failed to create dependent. Response status {0}", responseEntity.getStatusCode().value()));
                                        return new APIResponse(400, "fail", "Failed to create dependent. Response code " + responseEntity.getStatusCode().value(), Instant.now());
                                    }
                                }).onErrorResume(error -> {
                                    LOG.error(MessageFormat.format("Failed to create dependent {0}", error.getMessage()));
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create dependent", Instant.now()));
                                });
                    } else {
                        return Mono.just(new APIResponse(400, "fail", "Error creating dependent.", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if (apiResponse.getStatus() == 200) {
                LifeAPIResponse lifeAPIResponse = AppUtil.dtoToEntity(apiResponse.getData(),new LifeAPIResponse());
                /*return saveMember(lifeAPIResponse.getPolicyNumber(), memberDTO.getClient(), memberDTO.getAgentCode(), memberDTO.getPolicyNumber(), memberDTO.getBrokerCode(), memberDTO.getTitle(), memberDTO.getFirstName(), memberDTO.getSurname(), memberDTO.getIdNumber(), memberDTO.getGender(), AppUtil.formatDate(memberDTO.getDateOfBirth()),
                        memberDTO.getAge(), memberDTO.getCellNumber(), memberDTO.getAltCellNumber(), memberDTO.getWorkNumber(), memberDTO.getHomeNumber(), memberDTO.getEmail(), memberDTO.getFax(), memberDTO.getContactType(), memberDTO.getPostalAddress1(), memberDTO.getPostalAddress2(), memberDTO.getPostalAddress3(), memberDTO.getPostalCode(), memberDTO.getResidentialAddress1(),
                        memberDTO.getResidentialAddress2(), memberDTO.getResidentialAddress3(), memberDTO.getResidentialCode(), memberDTO.getMemberType(), memberDTO.getPremium(), memberDTO.getCover(), memberDTO.getAddPolicyID(), memberDTO.getStatusCode())
                        .then(Mono.just("next"))
                        .flatMap(msg->{
                            return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
                        });*/
                return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
            } else {
                return Mono.just(ResponseEntity.badRequest().body(apiResponse));
            }
        });
    }

    public Mono<ResponseEntity<APIResponse>> createExtendedMember(ExtendedMember extendedMemberDTO) {
        setConfigs(pol360EndpointUrl);

        return tokenService.getPol360APIToken().flatMap(
                bearerToken -> {
                    if (!bearerToken.isBlank() || !bearerToken.isEmpty()) {

                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.registerModule(new JavaTimeModule());
                        String formattedReq = null;
                        try {
                            formattedReq = objectMapper.writeValueAsString(extendedMemberDTO);
                        } catch (JsonProcessingException ex) {
                            return Mono.just(new APIResponse(400, "Failed", "Failed to get process request", Instant.now()));
                        }

                        return webClient.post()
                                .uri("/api/360API.php")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.ACCEPT, "*/*")
                                .header("Authorization", bearerToken)
                                .body(BodyInserters.fromObject(formattedReq))
                                .retrieve()
                                .toEntity(LifeAPIResponse.class).map(responseEntity -> {
                                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                                        LifeAPIResponse lifeAPIResponse = responseEntity.getBody();
                                        if (lifeAPIResponse != null && lifeAPIResponse.getResult()!=null && !lifeAPIResponse.getResult().toLowerCase().contains("err")) {
                                            String memberId = lifeAPIResponse != null ? String.valueOf(lifeAPIResponse.getMemberID()) : "";
                                            LOG.error(MessageFormat.format("Member successfully created member id{0}", memberId));
                                            return new APIResponse(200, "success", lifeAPIResponse, Instant.now());
                                        } else {
                                            LOG.error(MessageFormat.format("Failed to create member. Response status {0}", lifeAPIResponse.getMessage()));
                                            return new APIResponse(400, "fail", "Failed to create member. Response " + lifeAPIResponse.getMessage(), Instant.now());
                                        }
                                    } else {
                                        LOG.error(MessageFormat.format("Failed to create member. Response status {0}", responseEntity.getStatusCode().value()));
                                        return new APIResponse(400, "fail", "Failed to create member. Response code " + responseEntity.getStatusCode().value(), Instant.now());
                                    }
                                }).onErrorResume(error -> {
                                    LOG.error(MessageFormat.format("Failed to create member {0}", error.getMessage()));
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create member", Instant.now()));
                                });
                    } else {
                        return Mono.just(new APIResponse(400, "fail", "Error creating member.", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if (apiResponse.getStatus() == 200) {
                LifeAPIResponse lifeAPIResponse = AppUtil.dtoToEntity(apiResponse.getData(),new LifeAPIResponse());
                /*return saveMember(lifeAPIResponse.getPolicyNumber(), memberDTO.getClient(), memberDTO.getAgentCode(), memberDTO.getPolicyNumber(), memberDTO.getBrokerCode(), memberDTO.getTitle(), memberDTO.getFirstName(), memberDTO.getSurname(), memberDTO.getIdNumber(), memberDTO.getGender(), AppUtil.formatDate(memberDTO.getDateOfBirth()),
                        memberDTO.getAge(), memberDTO.getCellNumber(), memberDTO.getAltCellNumber(), memberDTO.getWorkNumber(), memberDTO.getHomeNumber(), memberDTO.getEmail(), memberDTO.getFax(), memberDTO.getContactType(), memberDTO.getPostalAddress1(), memberDTO.getPostalAddress2(), memberDTO.getPostalAddress3(), memberDTO.getPostalCode(), memberDTO.getResidentialAddress1(),
                        memberDTO.getResidentialAddress2(), memberDTO.getResidentialAddress3(), memberDTO.getResidentialCode(), memberDTO.getMemberType(), memberDTO.getPremium(), memberDTO.getCover(), memberDTO.getAddPolicyID(), memberDTO.getStatusCode())
                        .then(Mono.just("next"))
                        .flatMap(msg->{
                            return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
                        });*/
                return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
            } else {
                return Mono.just(ResponseEntity.badRequest().body(apiResponse));
            }
        });
    }

    public Mono<ResponseEntity<APIResponse>> createSpouse(Spouse spouseDTO) {
        setConfigs(pol360EndpointUrl);

        return tokenService.getPol360APIToken().flatMap(
                bearerToken -> {
                    if (!bearerToken.isBlank() || !bearerToken.isEmpty()) {

                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.registerModule(new JavaTimeModule());
                        String formattedReq = null;
                        try {
                            formattedReq = objectMapper.writeValueAsString(spouseDTO);
                        } catch (JsonProcessingException ex) {
                            return Mono.just(new APIResponse(400, "Failed", "Failed to get process request", Instant.now()));
                        }

                        return webClient.post()
                                .uri("/api/360API.php")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.ACCEPT, "*/*")
                                .header("Authorization", bearerToken)
                                .body(BodyInserters.fromObject(formattedReq))
                                .retrieve()
                                .toEntity(LifeAPIResponse.class).map(responseEntity -> {
                                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                                        LifeAPIResponse lifeAPIResponse = responseEntity.getBody();
                                        if (lifeAPIResponse != null && lifeAPIResponse.getResult()!=null && !lifeAPIResponse.getResult().toLowerCase().contains("err")) {
                                            String memberId = lifeAPIResponse != null ? String.valueOf(lifeAPIResponse.getMemberID()) : "";
                                            LOG.error(MessageFormat.format("Spouse successfully created id{0}", memberId));
                                            return new APIResponse(200, "success", lifeAPIResponse, Instant.now());
                                        } else {
                                            LOG.error(MessageFormat.format("Failed to create spouse. Response status {0}", lifeAPIResponse.getMessage()));
                                            return new APIResponse(400, "fail", "Failed to create spouse. Response " + lifeAPIResponse.getMessage(), Instant.now());
                                        }
                                    } else {
                                        LOG.error(MessageFormat.format("Failed to create spouse. Response status {0}", responseEntity.getStatusCode().value()));
                                        return new APIResponse(400, "fail", "Failed to create spouse. Response code " + responseEntity.getStatusCode().value(), Instant.now());
                                    }
                                }).onErrorResume(error -> {
                                    LOG.error(MessageFormat.format("Failed to create spouse {0}", error.getMessage()));
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create spouse", Instant.now()));
                                });
                    } else {
                        return Mono.just(new APIResponse(400, "fail", "Error creating spouse.", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if (apiResponse.getStatus() == 200) {
                LifeAPIResponse lifeAPIResponse = AppUtil.dtoToEntity(apiResponse.getData(),new LifeAPIResponse());
                /*return saveMember(lifeAPIResponse.getPolicyNumber(), memberDTO.getClient(), memberDTO.getAgentCode(), memberDTO.getPolicyNumber(), memberDTO.getBrokerCode(), memberDTO.getTitle(), memberDTO.getFirstName(), memberDTO.getSurname(), memberDTO.getIdNumber(), memberDTO.getGender(), AppUtil.formatDate(memberDTO.getDateOfBirth()),
                        memberDTO.getAge(), memberDTO.getCellNumber(), memberDTO.getAltCellNumber(), memberDTO.getWorkNumber(), memberDTO.getHomeNumber(), memberDTO.getEmail(), memberDTO.getFax(), memberDTO.getContactType(), memberDTO.getPostalAddress1(), memberDTO.getPostalAddress2(), memberDTO.getPostalAddress3(), memberDTO.getPostalCode(), memberDTO.getResidentialAddress1(),
                        memberDTO.getResidentialAddress2(), memberDTO.getResidentialAddress3(), memberDTO.getResidentialCode(), memberDTO.getMemberType(), memberDTO.getPremium(), memberDTO.getCover(), memberDTO.getAddPolicyID(), memberDTO.getStatusCode())
                        .then(Mono.just("next"))
                        .flatMap(msg->{
                            return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
                        });*/
                return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
            } else {
                return Mono.just(ResponseEntity.badRequest().body(apiResponse));
            }
        });
    }

    public Mono<ResponseEntity<APIResponse>> addBankDetails(BankDetails bankDetailsDTO) {
        setConfigs(pol360EndpointUrl);

        return tokenService.getPol360APIToken().flatMap(
                bearerToken -> {
                    if (!bearerToken.isBlank() || !bearerToken.isEmpty()) {

                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.registerModule(new JavaTimeModule());
                        String formattedReq = null;
                        try {
                            formattedReq = objectMapper.writeValueAsString(bankDetailsDTO);
                        } catch (JsonProcessingException ex) {
                            return Mono.just(new APIResponse(400, "Failed", "Failed to get process request", Instant.now()));
                        }

                        return webClient.post()
                                .uri("/api/360API.php")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.ACCEPT, "*/*")
                                .header("Authorization", bearerToken)
                                .body(BodyInserters.fromObject(formattedReq))
                                .retrieve()
                                .toEntity(LifeAPIResponse.class).map(responseEntity -> {
                                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                                        LifeAPIResponse lifeAPIResponse = responseEntity.getBody();
                                        if (lifeAPIResponse != null && lifeAPIResponse.getResult()!=null && !lifeAPIResponse.getResult().toLowerCase().contains("err")) {
                                            String memberId = lifeAPIResponse != null ? String.valueOf(lifeAPIResponse.getMemberID()) : "";
                                            LOG.error(MessageFormat.format("Member successfully created member id{0}", memberId));
                                            return new APIResponse(200, "success", lifeAPIResponse, Instant.now());
                                        } else {
                                            LOG.error(MessageFormat.format("Failed to create member. Response status {0}", lifeAPIResponse.getMessage()));
                                            return new APIResponse(400, "fail", "Failed to create member. Response " + lifeAPIResponse.getMessage(), Instant.now());
                                        }
                                    } else {
                                        LOG.error(MessageFormat.format("Failed to create member. Response status {0}", responseEntity.getStatusCode().value()));
                                        return new APIResponse(400, "fail", "Failed to create member. Response code " + responseEntity.getStatusCode().value(), Instant.now());
                                    }
                                }).onErrorResume(error -> {
                                    LOG.error(MessageFormat.format("Failed to create member {0}", error.getMessage()));
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create member", Instant.now()));
                                });
                    } else {
                        return Mono.just(new APIResponse(400, "fail", "Error creating member.", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if (apiResponse.getStatus() == 200) {
                LifeAPIResponse lifeAPIResponse = AppUtil.dtoToEntity(apiResponse.getData(),new LifeAPIResponse());
                /*return saveMember(lifeAPIResponse.getPolicyNumber(), memberDTO.getClient(), memberDTO.getAgentCode(), memberDTO.getPolicyNumber(), memberDTO.getBrokerCode(), memberDTO.getTitle(), memberDTO.getFirstName(), memberDTO.getSurname(), memberDTO.getIdNumber(), memberDTO.getGender(), AppUtil.formatDate(memberDTO.getDateOfBirth()),
                        memberDTO.getAge(), memberDTO.getCellNumber(), memberDTO.getAltCellNumber(), memberDTO.getWorkNumber(), memberDTO.getHomeNumber(), memberDTO.getEmail(), memberDTO.getFax(), memberDTO.getContactType(), memberDTO.getPostalAddress1(), memberDTO.getPostalAddress2(), memberDTO.getPostalAddress3(), memberDTO.getPostalCode(), memberDTO.getResidentialAddress1(),
                        memberDTO.getResidentialAddress2(), memberDTO.getResidentialAddress3(), memberDTO.getResidentialCode(), memberDTO.getMemberType(), memberDTO.getPremium(), memberDTO.getCover(), memberDTO.getAddPolicyID(), memberDTO.getStatusCode())
                        .then(Mono.just("next"))
                        .flatMap(msg->{
                            return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
                        });*/
                return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
            } else {
                return Mono.just(ResponseEntity.badRequest().body(apiResponse));
            }
        });
    }

    public Mono<ResponseEntity<APIResponse>> getProductsByCode(ProductDTO productDTO) {
        setConfigs(pol360EndpointUrl);

        return tokenService.getPol360APIToken().flatMap(
                bearerToken -> {
                    if (!bearerToken.isEmpty()) {
                        return webClient.method(HttpMethod.GET)
                                .uri("/api/ProductAPI.php?ClientName=" + productDTO.getClient() + "&Function=GetProductByCode&ProductCode="+productDTO.getProductCode())
                                //GetAllProducts
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.ACCEPT, "*/*")
                                .headers((headers -> headers.add("Authorization","Basic " + Base64.getEncoder().encodeToString((pol360UserName + ":" + pol360Password).getBytes()))))
                                .bodyValue(productDTO)
                                .retrieve()
                                .toEntity(ProductsResponse.class).map(responseEntity -> {
                                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                                        ProductsResponse productsResponse = responseEntity.getBody();
                                       if (productsResponse != null && productsResponse.getResult()!=null && !productsResponse.getResult().toLowerCase().contains("err")) {
                                            LOG.info("Successfully retrieved products");
                                           return new APIResponse(200, "success", productsResponse, Instant.now());
                                        } else {
                                            LOG.error("Error retrieving products");
                                            return new APIResponse(400, "fail", "Error retrieving products", Instant.now());
                                        }
                                    } else {
                                        LOG.error(MessageFormat.format("Failed to retrieve products. Response status {0}", responseEntity.getStatusCode().value()));
                                        return new APIResponse(400, "fail", "Failed to retrieve products. Response status " + responseEntity.getStatusCode().value(), Instant.now());
                                    }
                                }).onErrorResume(error -> {
                                    LOG.error(MessageFormat.format("Failed to retrieve products {0}", error.getMessage()));
                                    return Mono.just(new APIResponse(400, "fail", "Failed retrieve products ", Instant.now()));
                                });
                    } else {
                        return Mono.just(new APIResponse(400, "fail", "Error retrieving products", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
                    if(apiResponse.getStatus()==200) {
                        return Mono.just(ResponseEntity.ok().body(apiResponse));
                    }else{
                        return Mono.just(ResponseEntity.badRequest().body(apiResponse));
                    }
        });
    }

    Mono<String> saveMember(String pol360RefId, String client, String agentCode, String policyNumber, String brokerCode, String title, String firstName, String surname, String idNUmber, String gender, LocalDate dateOfBirth, String age, String cellNumber, String altCellNumber, String workNumber, String homeNumber, String email, String fax, String contactType, String postalAddress1, String postalAddress2, String postalAddress3, String postalCode, String residentialAddress1, String residentialAddress2, String residentialAddress3, String residentialCode, String memberType, String premium, String cover, String addPolicyId, String statusCode) {
        return lifeInsuranceRepository.saveMember(pol360RefId, client, agentCode, policyNumber, brokerCode, title, firstName, surname, idNUmber, gender, dateOfBirth, age, cellNumber, altCellNumber, workNumber, homeNumber, email, fax, contactType, postalAddress1, postalAddress2,
                        postalAddress3, postalCode, residentialAddress1, residentialAddress2, residentialAddress3, residentialCode, memberType, premium, cover, addPolicyId, statusCode
                ).then(Mono.just("next"))
                .flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed saving member details {0}", pol360RefId));
                    return Mono.just("Member details successfully saved");
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save member details. Error {0}", err.getMessage()));
                    return Mono.just("Failed to save member details");
                });
    }

    Mono<ResponseEntity<APIResponse>> saveSpouse(String client, String title, String firstName, String surname, String idNumber, String gender, LocalDate dateOfBirth, String age, String mainMemberId, String policyNumber) {
        return lifeInsuranceRepository.saveSpouse(client, title, firstName, surname, idNumber, gender, dateOfBirth, age, mainMemberId, policyNumber)
                .then(Mono.just("next"))
                .flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed saving spouse details {0}", mainMemberId));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200, "success", "Spouse details successfully saved", Instant.now())));
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save spouse details. Error {0}", err.getMessage()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(400, "fail", "Failed to save spouse details", Instant.now())));
                });

    }


    Mono<ResponseEntity<APIResponse>> saveExtendedDependent(String client, String title, String firstName, String surname, String idNumber, String gender, LocalDate dateOfBirth, String age, String mainMemberId, String policyNumber) {
        return lifeInsuranceRepository.saveExtendedDependent(client, title, firstName, surname, idNumber, gender, dateOfBirth, age, mainMemberId, policyNumber)
                .then(Mono.just("next"))
                .flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed saving external dependent details {0}", mainMemberId));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200, "success", "External member details successfully saved", Instant.now())));
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save external member details. Error {0}", err.getMessage()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(400, "fail", "Failed to save external member details", Instant.now())));
                });
    }

    Mono<ResponseEntity<APIResponse>> saveDependent(String client, String title, String firstName, String surname, String idNumber, String gender, LocalDate dateOfBirth, String age, String mainMemberId, String policyNumber) {
        return lifeInsuranceRepository.saveDependent(client, title, firstName, surname, idNumber, gender, dateOfBirth, age, mainMemberId, policyNumber)
                .then(Mono.just("next"))
                .flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed saving dependent details {0}", mainMemberId));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200, "success", "Dependent details successfully saved", Instant.now())));
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save dependent details. Error {0}", err.getMessage()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(400, "fail", "Failed to save dependent details", Instant.now())));
                });
    }

    Mono<ResponseEntity<APIResponse>> saveBeneficiary(String policyNumber, String client, String contactCell, String contactWorkTell, String contactHomeTell, String contactFax, String contactEmail, String sessionUserID, String prefType, String benLastName, String benFirstName, String benIdNumber, String benPercentage, LocalDate benDOB, String benRelation, String benType, String title) {
        return lifeInsuranceRepository.saveBeneficiary(policyNumber, client, contactCell, contactWorkTell, contactHomeTell, contactFax, contactEmail, sessionUserID, prefType, benLastName, benFirstName, benIdNumber, benPercentage, benDOB, benRelation, benType, title)
                .then(Mono.just("next"))
                .flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed saving beneficiary details {0}", policyNumber));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200, "success", "Beneficiary details successfully saved", Instant.now())));
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save beneficiary details. Error {0}", err.getMessage()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(400, "fail", "Failed to save beneficiary details", Instant.now())));
                });
    }

    Mono<ResponseEntity<APIResponse>> saveBankDetails(String client, String policyNumber, String sessionUserID, String branchCode, String accNumber, String accType, String accName, String dedDay, LocalDate fromDate, String idNumber, String subNaedo) {
        return lifeInsuranceRepository.saveBankDetails(client, policyNumber, sessionUserID, branchCode, accNumber, accType, accName, dedDay, fromDate, idNumber, subNaedo)
                .then(Mono.just("next"))
                .flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed saving bank details details {0}", policyNumber));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200, "success", "Bank details successfully saved", Instant.now())));
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save bank details. Error {0}", err.getMessage()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(400, "fail", "Failed to save bank details details", Instant.now())));
                });
    }

}
