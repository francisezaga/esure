package com.egroupx.esure.services;

import com.egroupx.esure.dto.v360.ProductDTO;
import com.egroupx.esure.exceptions.LifeAPIErrorException;
import com.egroupx.esure.exceptions.LifeAPIErrorHandler;
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

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Instant;
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
                                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class) // error body as String or other class
                                        .flatMap(error -> {
                                            LOG.error(error);
                                            return Mono.error(new LifeAPIErrorException(error));
                                        }))
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
                                    String errorMsg = LifeAPIErrorHandler.handleLifeAPIError(error.getMessage()).isEmpty()? error.getMessage() : LifeAPIErrorHandler.handleLifeAPIError(error.getMessage());
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create member."+errorMsg, Instant.now()));
                                });
                    } else {
                        return Mono.just(new APIResponse(400, "fail", "Error creating member.", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if (apiResponse.getStatus() == 200) {
                LifeAPIResponse lifeAPIResponse = AppUtil.dtoToEntity(apiResponse.getData(),new LifeAPIResponse());
                return saveMember(memberDTO)
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
                                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class) // error body as String or other class
                                        .flatMap(error -> {
                                            LOG.error(error);
                                            return Mono.error(new LifeAPIErrorException(error));
                                        }))
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
                                        LOG.error(MessageFormat.format("Failed to create beneficiary member. Response status {0}", responseEntity.getStatusCode().value()));
                                        return new APIResponse(400, "fail", "Failed to create beneficiary member. Response code " + responseEntity.getStatusCode().value(), Instant.now());
                                    }
                                }).onErrorResume(error -> {
                                    LOG.error(MessageFormat.format("Failed to create beneficiary member {0}", error.getMessage()));
                                    String errorMsg = LifeAPIErrorHandler.handleLifeAPIError(error.getMessage()).isEmpty()? error.getMessage() : LifeAPIErrorHandler.handleLifeAPIError(error.getMessage());
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create beneficiary  member. "+ errorMsg, Instant.now()));
                                });
                    } else {
                        return Mono.just(new APIResponse(400, "fail", "Error creating beneficiary member.", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if (apiResponse.getStatus() == 200) {
                LifeAPIResponse lifeAPIResponse = AppUtil.dtoToEntity(apiResponse.getData(),new LifeAPIResponse());
                return saveBeneficiary(beneficiaryDTO)
                        .then(Mono.just("next"))
                        .flatMap(msg->{
                            return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
                        });
                //return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
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
                                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class) // error body as String or other class
                                        .flatMap(error -> {
                                            LOG.error(error);
                                            return Mono.error(new LifeAPIErrorException(error));
                                        })) // throw a functional exception
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
                                    LOG.error(MessageFormat.format("Failed to create dependent. {0} ", error.getMessage()));
                                    String errorMsg = LifeAPIErrorHandler.handleLifeAPIError(error.getMessage()).isEmpty()? error.getMessage() : LifeAPIErrorHandler.handleLifeAPIError(error.getMessage());
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create dependent "+ errorMsg, Instant.now()));
                                });
                    } else {
                        return Mono.just(new APIResponse(400, "fail", "Error creating dependent.", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if (apiResponse.getStatus() == 200) {
                LifeAPIResponse lifeAPIResponse = AppUtil.dtoToEntity(apiResponse.getData(),new LifeAPIResponse());
                return saveDependent(dependentDTO).
                        then(Mono.just("next"))
                        .flatMap(msg->{
                            return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
                        });
               // return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
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
                                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class) // error body as String or other class
                                        .flatMap(error -> {
                                            LOG.error(error);
                                            return Mono.error(new LifeAPIErrorException(error));
                                        }))
                                .toEntity(LifeAPIResponse.class).map(responseEntity -> {
                                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                                        LifeAPIResponse lifeAPIResponse = responseEntity.getBody();
                                        if (lifeAPIResponse != null && lifeAPIResponse.getResult()!=null && !lifeAPIResponse.getResult().toLowerCase().contains("err")) {
                                            String memberId = lifeAPIResponse != null ? String.valueOf(lifeAPIResponse.getMemberID()) : "";
                                            LOG.error(MessageFormat.format("Extended member successfully created member id{0}", memberId));
                                            return new APIResponse(200, "success", lifeAPIResponse, Instant.now());
                                        } else {
                                            String errorMsg = "";
                                            if(lifeAPIResponse!=null){
                                                errorMsg=lifeAPIResponse.getMessage();
                                            }
                                            LOG.error(MessageFormat.format("Failed to create extended member. Response status {0}", errorMsg));
                                            return new APIResponse(400, "fail", "Extended member could not be created. Response " + errorMsg, Instant.now());
                                        }
                                    } else {
                                        LOG.error(MessageFormat.format("Failed to create extended member. Response status {0}", responseEntity.getStatusCode().value()));
                                        return new APIResponse(400, "fail", "Failed to create extended member. Response code " + responseEntity.getStatusCode().value(), Instant.now());
                                    }
                                }).onErrorResume(error -> {
                                    LOG.error(MessageFormat.format("Failed to create extended member {0}", error.getMessage()));
                                    String errorMsg = LifeAPIErrorHandler.handleLifeAPIError(error.getMessage()).isEmpty()? error.getMessage() : LifeAPIErrorHandler.handleLifeAPIError(error.getMessage());
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create member. " + errorMsg, Instant.now()));
                                });
                    } else {
                        return Mono.just(new APIResponse(400, "fail", "Error creating extended member.", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if (apiResponse.getStatus() == 200) {
                LifeAPIResponse lifeAPIResponse = AppUtil.dtoToEntity(apiResponse.getData(),new LifeAPIResponse());
                return saveExtendedDependent(extendedMemberDTO)
                        .then(Mono.just("next"))
                        .flatMap(msg->{
                            return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
                        });
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
                                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class) // error body as String or other class
                                        .flatMap(error -> {
                                            LOG.error(error);
                                            return Mono.error(new LifeAPIErrorException(error));
                                        }))
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
                                    String errorMsg = LifeAPIErrorHandler.handleLifeAPIError(error.getMessage()).isEmpty()? error.getMessage() : LifeAPIErrorHandler.handleLifeAPIError(error.getMessage());
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create spouse. "+ errorMsg, Instant.now()));
                                });
                    } else {
                        return Mono.just(new APIResponse(400, "fail", "Error creating spouse.", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if (apiResponse.getStatus() == 200) {
                LifeAPIResponse lifeAPIResponse = AppUtil.dtoToEntity(apiResponse.getData(),new LifeAPIResponse());
                return saveSpouse(spouseDTO).then(Mono.just("next"))
                        .flatMap(msg->{
                            return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
                        });
               // return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
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
                                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class) // error body as String or other class
                                        .flatMap(error -> {
                                            LOG.error(error);
                                            return Mono.error(new LifeAPIErrorException(error));
                                        }))
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
                                        LOG.error(MessageFormat.format("Failed to add bank details. Response status {0}", responseEntity.getStatusCode().value()));
                                        return new APIResponse(400, "fail", "Failed to add bank details. Response code " + responseEntity.getStatusCode().value(), Instant.now());
                                    }
                                }).onErrorResume(error -> {
                                    LOG.error(MessageFormat.format("Failed to add bank details {0}", error.getMessage()));
                                    String errorMsg = LifeAPIErrorHandler.handleLifeAPIError(error.getMessage()).isEmpty()? error.getMessage() : LifeAPIErrorHandler.handleLifeAPIError(error.getMessage());
                                    return Mono.just(new APIResponse(400, "fail", "Failed to add bank details. " + errorMsg, Instant.now()));
                                });
                    } else {
                        return Mono.just(new APIResponse(400, "fail", "Error adding bank details.", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if (apiResponse.getStatus() == 200) {
                LifeAPIResponse lifeAPIResponse = AppUtil.dtoToEntity(apiResponse.getData(),new LifeAPIResponse());
                return saveBankDetails(bankDetailsDTO).then(Mono.just("next"))
                        .flatMap(msg->{
                            return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
                        });
               // return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
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

    Mono<String> saveMember(Member memberDTO) {
        return lifeInsuranceRepository.saveMember(memberDTO.getPolicyNumber(), memberDTO.getClient(),memberDTO.getAgentCode(), memberDTO.getPolicyNumber(), memberDTO.getBrokerCode(), memberDTO.getTitle(), memberDTO.getFirstName(), memberDTO.getSurname(),memberDTO.getIdNumber(), memberDTO.getGender(),AppUtil.formatDate(memberDTO.getDateOfBirth()),memberDTO.getAge(),memberDTO.getCellNumber(),memberDTO.getAltCellNumber(),memberDTO.getWorkNumber(), memberDTO.getHomeNumber(), memberDTO.getEmail(), memberDTO.getFax(), memberDTO.getContactType(),memberDTO.getPostalAddress1(),memberDTO.getPostalAddress2(),
                        memberDTO.getPostalAddress3(), memberDTO.getPostalCode(), memberDTO.getResidentialAddress1(), memberDTO.getResidentialAddress2(), memberDTO.getResidentialAddress3(), memberDTO.getResidentialCode(), memberDTO.getMemberType(), memberDTO.getPremium(), memberDTO.getCover(), memberDTO.getAddPolicyID(), memberDTO.getStatusCode()
                ).then(Mono.just("next"))
                .flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed saving member details {0}", memberDTO.getPolicyNumber()));
                    return Mono.just("Member details successfully saved");
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save member details. Error {0}", err.getMessage()));
                    return Mono.just("Failed to save member details");
                });
    }

    Mono<ResponseEntity<APIResponse>> saveSpouse(Spouse spouseDTO) {
        return lifeInsuranceRepository.saveSpouse(spouseDTO.getClient(), spouseDTO.getTitle(),spouseDTO.getFirstName(), spouseDTO.getSurname(), spouseDTO.getIdNumber(), spouseDTO.getGender(),AppUtil.formatDate(spouseDTO.getDateOfBirth()), spouseDTO.getAge(), spouseDTO.getMainMemberID(), spouseDTO.getPolicyNumber())
                .then(Mono.just("next"))
                .flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed saving spouse details {0}", spouseDTO.getMainMemberID()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200, "success", "Spouse details successfully saved", Instant.now())));
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save spouse details. Error {0}", err.getMessage()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(400, "fail", "Failed to save spouse details", Instant.now())));
                });

    }

    Mono<ResponseEntity<APIResponse>> saveExtendedDependent(ExtendedMember extendedMemberDto) {
        return lifeInsuranceRepository.saveExtendedDependent(extendedMemberDto.getClient(),extendedMemberDto.getTitle(),extendedMemberDto.getFirstName(), extendedMemberDto.getSurname(), extendedMemberDto.getIdNumber(), extendedMemberDto.getGender(), AppUtil.formatDate(extendedMemberDto.getDateOfBirth()), extendedMemberDto.getAge(), extendedMemberDto.getMainMemberID(), extendedMemberDto.getPolicyNumber())
                .then(Mono.just("next"))
                .flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed saving external dependent details {0}", extendedMemberDto.getMainMemberID()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200, "success", "External member details successfully saved", Instant.now())));
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save external member details. Error {0}", err.getMessage()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(400, "fail", "Failed to save external member details", Instant.now())));
                });
    }

    Mono<ResponseEntity<APIResponse>> saveDependent(Dependent dependentDTO) {
        return lifeInsuranceRepository.saveDependent(dependentDTO.getClient(), dependentDTO.getTitle(), dependentDTO.getFirstName(), dependentDTO.getSurname(), dependentDTO.getIdNumber(), dependentDTO.getGender(),AppUtil.formatDate(dependentDTO.getDateOfBirth()), dependentDTO.getAge(), dependentDTO.getMainMemberID(), dependentDTO.getPolicyNumber())
                .then(Mono.just("next"))
                .flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed saving dependent details {0}", dependentDTO));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200, "success", "Dependent details successfully saved", Instant.now())));
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save dependent details. Error {0}", err.getMessage()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(400, "fail", "Failed to save dependent details", Instant.now())));
                });
    }

    Mono<ResponseEntity<APIResponse>> saveBeneficiary(Beneficiary beneficiaryDTO) {
        return lifeInsuranceRepository.saveBeneficiary(beneficiaryDTO.getPolicyNumber(), beneficiaryDTO.getClient(), beneficiaryDTO.getContactCell(), beneficiaryDTO.getContactWorkTell(), beneficiaryDTO.getContactHomeTell(), beneficiaryDTO.getContactFax(), beneficiaryDTO.getContactEmail(), beneficiaryDTO.getSessionUserID(), beneficiaryDTO.getPrefType(), beneficiaryDTO.getBenLastName(), beneficiaryDTO.getBenFirstName(), beneficiaryDTO.getBenIDNumber(), beneficiaryDTO.getBenPercentage(), AppUtil.formatDate(beneficiaryDTO.getBenDOB()), beneficiaryDTO.getBenRelation(), beneficiaryDTO.getBenType(), beneficiaryDTO.getTitle())
                .then(Mono.just("next"))
                .flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed saving beneficiary details {0}", beneficiaryDTO.getBenIDNumber()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200, "success", "Beneficiary details successfully saved", Instant.now())));
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save beneficiary details. Error {0}", err.getMessage()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(400, "fail", "Failed to save beneficiary details", Instant.now())));
                });
    }

    Mono<ResponseEntity<APIResponse>> saveBankDetails(BankDetails bankDetailsDTO) {
        return lifeInsuranceRepository.saveBankDetails(bankDetailsDTO.getClient(), bankDetailsDTO.getPolicyNumber(), bankDetailsDTO.getSessionUserID(), bankDetailsDTO.getBranchCode(), bankDetailsDTO.getAccNumber(), bankDetailsDTO.getAccType(), bankDetailsDTO.getAccName(), bankDetailsDTO.getDedDay(),bankDetailsDTO.getFromDate(), bankDetailsDTO.getIdNumber(), bankDetailsDTO.getSubNaedo())
                .then(Mono.just("next"))
                .flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed saving bank details details {0}", bankDetailsDTO.getIdNumber()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200, "success", "Bank details successfully saved", Instant.now())));
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save bank details. Error {0}", err.getMessage()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(400, "fail", "Failed to save bank details details", Instant.now())));
                });
    }

}
