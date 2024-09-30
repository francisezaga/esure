package com.egroupx.esure.services;

import com.egroupx.esure.dto.life.*;
import com.egroupx.esure.dto.v360.ProductDTO;
import com.egroupx.esure.enums.DOCType;
import com.egroupx.esure.exceptions.LifeAPIErrorException;
import com.egroupx.esure.exceptions.LifeAPIErrorHandler;
import com.egroupx.esure.model.life.Member;
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
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class LifeInsuranceService {

    @Value("${egroupx.services.pol360.endpointUrl}")
    private String pol360EndpointUrl;

    @Value("${egroupx.services.pol360.userName}")
    private String pol360UserName;

    @Value("${egroupx.services.pol360.password}")
    private String pol360Password;

    @Value("${egroupx.files.dir}")
    private String fileUploadDir;

    private WebClient webClient;

    private final TokenService tokenService;

    private final LifeInsuranceRepository lifeInsuranceRepository;

    private final AuthService authService;

    private final EmailService emailService;

    private final Logger LOG = LoggerFactory.getLogger(LifeInsuranceService.class);

    public LifeInsuranceService(TokenService tokenService, LifeInsuranceRepository lifeInsuranceRepository, AuthService authService, EmailService emailService) {
        this.tokenService = tokenService;
        this.lifeInsuranceRepository = lifeInsuranceRepository;
        this.authService = authService;
        this.emailService = emailService;
    }

    private void setConfigs(String endpointUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(endpointUrl)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE,
                        "Content-Type", MediaType.APPLICATION_JSON_VALUE
                ).build();
    }

    public Mono<ResponseEntity<APIResponse>> createMember(MemberDTO memberDTO) {
        setConfigs(pol360EndpointUrl);

        lifeInsuranceRepository.getLatestPolicyNumber().flatMap(polId -> {
            String policyId = generatePolicyId(polId);
            memberDTO.setPolicyNumber(String.valueOf(policyId));
            return Mono.just(polId);
        }).switchIfEmpty(Mono.defer(() -> {
            String policyId = generatePolicyId("");
            memberDTO.setPolicyNumber(String.valueOf(policyId));
            return Mono.just("next");
        })).onErrorResume(error -> {
            String policyId = generatePolicyId("");
            memberDTO.setPolicyNumber(String.valueOf(policyId));
            return Mono.just("next");
        }).subscribe();

        return authService.getCellVerificationDetails(memberDTO.getCellNumber()).flatMap(cellVerRes -> {
            if (cellVerRes.getStatus() == 200) {

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
                                                if (lifeAPIResponse != null && lifeAPIResponse.getResult() != null && !lifeAPIResponse.getResult().toLowerCase().contains("err")) {
                                                    String memberId = lifeAPIResponse != null ? String.valueOf(lifeAPIResponse.getMemberID()) : "";
                                                    LOG.error(MessageFormat.format("Member successfully created member with id{0}", memberId));
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
                                            String errorMsg = LifeAPIErrorHandler.handleLifeAPIError(error.getMessage()).isEmpty() ? error.getMessage() : LifeAPIErrorHandler.handleLifeAPIError(error.getMessage());
                                            return Mono.just(new APIResponse(400, "fail", "Failed to create member." + errorMsg, Instant.now()));
                                        });
                            } else {
                                return Mono.just(new APIResponse(400, "fail", "Error creating member.", Instant.now()));
                            }
                        }).flatMap(apiResponse -> {
                    if (apiResponse.getStatus() == 200) {
                        LifeAPIResponse lifeAPIResponse = AppUtil.dtoToEntity(apiResponse.getData(), new LifeAPIResponse());
                        String memberID = "";
                        if (lifeAPIResponse != null && lifeAPIResponse.getMemberID() != null) {
                            try {
                                memberID = String.valueOf(lifeAPIResponse.getMemberID());
                            } catch (Exception ex) {

                            }
                        }
                        return saveORUpdateMember(memberID, memberDTO)
                                .then(Mono.just("next"))
                                .flatMap(msg -> {
                                    return Mono.just(ResponseEntity.ok(new APIResponse(200, "success", lifeAPIResponse, Instant.now())));
                                });
                    } else {
                        return Mono.just(ResponseEntity.badRequest().body(apiResponse));
                    }
                });
            } else {
                return Mono.just(ResponseEntity.status(401).body(new APIResponse(401, "Unauthorized", "Cellphone number is not verified.Please verify your cell number and try again", Instant.now())));
            }
        });
    }

    public Mono<ResponseEntity<APIResponse>> saveMemberPersonalDetails(MemberDTO memberDTO) {

        return authService.getCellVerificationDetails(memberDTO.getCellNumber()).flatMap(cellVerRes -> {
            if (cellVerRes.getStatus() == 200) {

                return savePersonalDetails(memberDTO)
                        .flatMap(msg -> {
                            return Mono.just(ResponseEntity.ok(new APIResponse(200, "success",msg, Instant.now())));
                        });
            } else {
                return Mono.just(ResponseEntity.badRequest().body(cellVerRes));
            }
        });
    }

    public Mono<ResponseEntity<APIResponse>> createBeneficiary(BeneficiaryDTO beneficiaryDTO) {
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
                                        if (lifeAPIResponse != null && lifeAPIResponse.getResult() != null && !lifeAPIResponse.getResult().toLowerCase().contains("err")) {
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
                                    String errorMsg = LifeAPIErrorHandler.handleLifeAPIError(error.getMessage()).isEmpty() ? error.getMessage() : LifeAPIErrorHandler.handleLifeAPIError(error.getMessage());
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create beneficiary  member. " + errorMsg, Instant.now()));
                                });
                    } else {
                        return Mono.just(new APIResponse(400, "fail", "Error creating beneficiary member.", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if (apiResponse.getStatus() == 200) {
                LifeAPIResponse lifeAPIResponse = AppUtil.dtoToEntity(apiResponse.getData(), new LifeAPIResponse());
                return saveBeneficiary(beneficiaryDTO)
                        .then(Mono.just("next"))
                        .flatMap(msg -> {
                            return Mono.just(ResponseEntity.ok(new APIResponse(200, "success", lifeAPIResponse, Instant.now())));
                        });
                //return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
            } else {
                return Mono.just(ResponseEntity.badRequest().body(apiResponse));
            }
        });
    }

    public Mono<ResponseEntity<APIResponse>> createDependent(DependentDTO dependentDTO) {
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
                                        if (lifeAPIResponse != null && lifeAPIResponse.getResult() != null && !lifeAPIResponse.getResult().toLowerCase().contains("err")) {
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
                                    String errorMsg = LifeAPIErrorHandler.handleLifeAPIError(error.getMessage()).isEmpty() ? error.getMessage() : LifeAPIErrorHandler.handleLifeAPIError(error.getMessage());
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create dependent " + errorMsg, Instant.now()));
                                });
                    } else {
                        return Mono.just(new APIResponse(400, "fail", "Error creating dependent.", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if (apiResponse.getStatus() == 200) {
                LifeAPIResponse lifeAPIResponse = AppUtil.dtoToEntity(apiResponse.getData(), new LifeAPIResponse());
                return saveDependent(dependentDTO).
                        then(Mono.just("next"))
                        .flatMap(msg -> {
                            return Mono.just(ResponseEntity.ok(new APIResponse(200, "success", lifeAPIResponse, Instant.now())));
                        });
                // return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
            } else {
                return Mono.just(ResponseEntity.badRequest().body(apiResponse));
            }
        });
    }

    public Mono<ResponseEntity<APIResponse>> createExtendedMember(ExtendedMemberDTO extendedMemberDTO) {
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
                                        if (lifeAPIResponse != null && lifeAPIResponse.getResult() != null && !lifeAPIResponse.getResult().toLowerCase().contains("err")) {
                                            String memberId = lifeAPIResponse != null ? String.valueOf(lifeAPIResponse.getMemberID()) : "";
                                            LOG.error(MessageFormat.format("Extended member successfully created member id{0}", memberId));
                                            return new APIResponse(200, "success", lifeAPIResponse, Instant.now());
                                        } else {
                                            String errorMsg = "";
                                            if (lifeAPIResponse != null) {
                                                errorMsg = lifeAPIResponse.getMessage();
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
                                    String errorMsg = LifeAPIErrorHandler.handleLifeAPIError(error.getMessage()).isEmpty() ? error.getMessage() : LifeAPIErrorHandler.handleLifeAPIError(error.getMessage());
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create member. " + errorMsg, Instant.now()));
                                });
                    } else {
                        return Mono.just(new APIResponse(400, "fail", "Error creating extended member.", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if (apiResponse.getStatus() == 200) {
                LifeAPIResponse lifeAPIResponse = AppUtil.dtoToEntity(apiResponse.getData(), new LifeAPIResponse());
                return saveExtendedDependent(extendedMemberDTO)
                        .then(Mono.just("next"))
                        .flatMap(msg -> {
                            return Mono.just(ResponseEntity.ok(new APIResponse(200, "success", lifeAPIResponse, Instant.now())));
                        });
            } else {
                return Mono.just(ResponseEntity.badRequest().body(apiResponse));
            }
        });
    }

    public Mono<ResponseEntity<APIResponse>> createSpouse(SpouseDTO spouseDTO) {
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
                                        if (lifeAPIResponse != null && lifeAPIResponse.getResult() != null && !lifeAPIResponse.getResult().toLowerCase().contains("err")) {
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
                                    String errorMsg = LifeAPIErrorHandler.handleLifeAPIError(error.getMessage()).isEmpty() ? error.getMessage() : LifeAPIErrorHandler.handleLifeAPIError(error.getMessage());
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create spouse. " + errorMsg, Instant.now()));
                                });
                    } else {
                        return Mono.just(new APIResponse(400, "fail", "Error creating spouse.", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if (apiResponse.getStatus() == 200) {
                LifeAPIResponse lifeAPIResponse = AppUtil.dtoToEntity(apiResponse.getData(), new LifeAPIResponse());
                return saveSpouse(spouseDTO).then(Mono.just("next"))
                        .flatMap(msg -> {
                            return Mono.just(ResponseEntity.ok(new APIResponse(200, "success", lifeAPIResponse, Instant.now())));
                        });
                // return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
            } else {
                return Mono.just(ResponseEntity.badRequest().body(apiResponse));
            }
        });
    }

    public Mono<ResponseEntity<APIResponse>> addBankDetails(BankDetailsDTO bankDetailsDTO) {
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
                                        if (lifeAPIResponse != null && lifeAPIResponse.getResult() != null && !lifeAPIResponse.getResult().toLowerCase().contains("err")) {
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
                                    String errorMsg = LifeAPIErrorHandler.handleLifeAPIError(error.getMessage()).isEmpty() ? error.getMessage() : LifeAPIErrorHandler.handleLifeAPIError(error.getMessage());
                                    return Mono.just(new APIResponse(400, "fail", "Failed to add bank details. " + errorMsg, Instant.now()));
                                });
                    } else {
                        return Mono.just(new APIResponse(400, "fail", "Error adding bank details.", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if (apiResponse.getStatus() == 200) {
                LifeAPIResponse lifeAPIResponse = AppUtil.dtoToEntity(apiResponse.getData(), new LifeAPIResponse());
                return saveBankDetails(bankDetailsDTO).then(Mono.just("next"))
                        .flatMap(msg -> {
                            return Mono.just(ResponseEntity.ok(new APIResponse(200, "success", lifeAPIResponse, Instant.now())));
                        });
                // return Mono.just(ResponseEntity.ok(new APIResponse(200,"success",lifeAPIResponse,Instant.now())));
            } else {
                return Mono.just(ResponseEntity.badRequest().body(apiResponse));
            }
        });
    }

    public Mono<ResponseEntity<APIResponse>> findProductsByCode(ProductDTO productDTO) {
        setConfigs(pol360EndpointUrl);

        return tokenService.getPol360APIToken().flatMap(
                bearerToken -> {
                    if (!bearerToken.isEmpty()) {
                        return webClient.method(HttpMethod.GET)
                                .uri("/api/ProductAPI.php?ClientName=" + productDTO.getClient() + "&Function=GetProductByCode&ProductCode=" + productDTO.getProductCode())
                                //GetAllProducts
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.ACCEPT, "*/*")
                                .headers((headers -> headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString((pol360UserName + ":" + pol360Password).getBytes()))))
                                .bodyValue(productDTO)
                                .retrieve()
                                .toEntity(ProductsResponse.class).map(responseEntity -> {
                                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                                        ProductsResponse productsResponse = responseEntity.getBody();
                                        if (productsResponse != null && productsResponse.getResult() != null && !productsResponse.getResult().toLowerCase().contains("err")) {
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
            if (apiResponse.getStatus() == 200) {
                return Mono.just(ResponseEntity.ok().body(apiResponse));
            } else {
                return Mono.just(ResponseEntity.badRequest().body(apiResponse));
            }
        });
    }

    Mono<String> saveORUpdateMember(String memberID, MemberDTO memberDTO) {
        return lifeInsuranceRepository.findMemberLastRecordByIdNumber(memberDTO.getIdNumber().trim())
                .flatMap(member -> {
                    LOG.info(MessageFormat.format("Member already exist. Updating member {0}", memberDTO.getIdNumber()));
                    return updateMember(memberID, memberDTO);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    LOG.error(MessageFormat.format("Member does not existing {0}. Saving member ", memberDTO.getIdNumber()));
                    return saveMember(memberID, memberDTO);
                }))
                .onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save or update member details. Error {0}", err.getMessage()));
                    return Mono.just("Failed to save or update member details");
                });
    }

    Mono<String> saveMember(String memberID, MemberDTO memberDTO) {
        return lifeInsuranceRepository.saveMember(memberID, memberDTO.getClient(), memberDTO.getAgentCode(), memberDTO.getPolicyNumber(), memberDTO.getBrokerCode(), memberDTO.getTitle(), memberDTO.getFirstName(), memberDTO.getSurname(), memberDTO.getIdNumber(), memberDTO.getGender(), AppUtil.formatDate(memberDTO.getDateOfBirth()), memberDTO.getAge(), memberDTO.getCellNumber(), memberDTO.getAltCellNumber(), memberDTO.getWorkNumber(), memberDTO.getHomeNumber(), memberDTO.getEmail(), memberDTO.getFax(), memberDTO.getContactType(), memberDTO.getPostalAddress1(), memberDTO.getPostalAddress2(),
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

    Mono<String> updateMember(String memberID, MemberDTO memberDTO) {
        return lifeInsuranceRepository.updateMember(memberID, memberDTO.getClient(), memberDTO.getAgentCode(), memberDTO.getPolicyNumber(), memberDTO.getBrokerCode(), memberDTO.getTitle(), memberDTO.getFirstName(), memberDTO.getSurname(), memberDTO.getIdNumber(), memberDTO.getGender(), AppUtil.formatDate(memberDTO.getDateOfBirth()), memberDTO.getAge(), memberDTO.getCellNumber(), memberDTO.getAltCellNumber(), memberDTO.getWorkNumber(), memberDTO.getHomeNumber(), memberDTO.getEmail(), memberDTO.getFax(), memberDTO.getContactType(), memberDTO.getPostalAddress1(), memberDTO.getPostalAddress2(),
                        memberDTO.getPostalAddress3(), memberDTO.getPostalCode(), memberDTO.getResidentialAddress1(), memberDTO.getResidentialAddress2(), memberDTO.getResidentialAddress3(), memberDTO.getResidentialCode(), memberDTO.getMemberType(), memberDTO.getPremium(), memberDTO.getCover(), memberDTO.getAddPolicyID(), memberDTO.getStatusCode()
                ).then(Mono.just("next"))
                .flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed updating member details {0}", memberDTO.getPolicyNumber()));
                    return Mono.just("Member details successfully updated");
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to update member details. Error {0}", err.getMessage()));
                    return Mono.just("Failed to update member details");
                });
    }

    Mono<String> savePersonalDetails(MemberDTO memberDTO) {

        return lifeInsuranceRepository.findMemberLastRecordByIdNumber(memberDTO.getIdNumber().trim())
                .flatMap(member -> {
                    LOG.info(MessageFormat.format("Member already exist. {0}", memberDTO.getIdNumber()));
                    return Mono.just("Personal details already exist");
                })
                .switchIfEmpty(Mono.defer(() -> {
                    LOG.error(MessageFormat.format("Member does not existing {0}. Saving member ", memberDTO.getIdNumber()));
                    return lifeInsuranceRepository.saveMemberPersonalDetails(memberDTO.getTitle(), memberDTO.getFirstName(), memberDTO.getSurname(), memberDTO.getIdNumber(), memberDTO.getGender(), AppUtil.formatDate(memberDTO.getDateOfBirth()), memberDTO.getAge(), memberDTO.getCellNumber(), memberDTO.getAltCellNumber(), memberDTO.getWorkNumber(), memberDTO.getHomeNumber(), memberDTO.getEmail(), memberDTO.getContactType()).then(Mono.just("next"))
                            .flatMap(msg -> {
                                LOG.info(MessageFormat.format("Completed saving member personal details {0}", memberDTO.getIdNumber()));
                                return sendEmailLifeCoverNotification(memberDTO.getIdNumber()).flatMap(res-> {
                                    return Mono.just("Personal details saved");
                                });
                            }).onErrorResume(err -> {
                                LOG.error(MessageFormat.format("Failed to save member personal details. Error {0}", err.getMessage()));
                                return Mono.just("Failed to save member personal details");
                            });
                }))
                .onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to saving member details. Error {0}", err.getMessage()));
                    return Mono.just("Failed to saving member details");
                });
    }

    Mono<ResponseEntity<APIResponse>> saveSpouse(SpouseDTO spouseDTO) {
        return lifeInsuranceRepository.saveSpouse(spouseDTO.getClient(), spouseDTO.getTitle(), spouseDTO.getFirstName(), spouseDTO.getSurname(), spouseDTO.getIdNumber(), spouseDTO.getGender(), AppUtil.formatDate(spouseDTO.getDateOfBirth()), spouseDTO.getAge(), spouseDTO.getMainMemberID(), spouseDTO.getPolicyNumber())
                .then(Mono.just("next"))
                .flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed saving spouse details {0}", spouseDTO.getMainMemberID()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200, "success", "Spouse details successfully saved", Instant.now())));
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save spouse details. Error {0}", err.getMessage()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(400, "fail", "Failed to save spouse details", Instant.now())));
                });

    }

    Mono<ResponseEntity<APIResponse>> saveExtendedDependent(ExtendedMemberDTO extendedMemberDto) {
        return lifeInsuranceRepository.saveExtendedDependent(extendedMemberDto.getClient(), extendedMemberDto.getTitle(), extendedMemberDto.getFirstName(), extendedMemberDto.getSurname(), extendedMemberDto.getIdNumber(), extendedMemberDto.getGender(), AppUtil.formatDate(extendedMemberDto.getDateOfBirth()), extendedMemberDto.getAge(), extendedMemberDto.getMainMemberID(), extendedMemberDto.getPolicyNumber(), extendedMemberDto.getRelation())
                .then(Mono.just("next"))
                .flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed saving external dependent details {0}", extendedMemberDto.getMainMemberID()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200, "success", "External member details successfully saved", Instant.now())));
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save external member details. Error {0}", err.getMessage()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(400, "fail", "Failed to save external member details", Instant.now())));
                });
    }

    Mono<ResponseEntity<APIResponse>> saveDependent(DependentDTO dependentDTO) {
        return lifeInsuranceRepository.saveDependent(dependentDTO.getClient(), dependentDTO.getTitle(), dependentDTO.getFirstName(), dependentDTO.getSurname(), dependentDTO.getIdNumber(), dependentDTO.getGender(), AppUtil.formatDate(dependentDTO.getDateOfBirth()), dependentDTO.getAge(), dependentDTO.getMainMemberID(), dependentDTO.getPolicyNumber())
                .then(Mono.just("next"))
                .flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed saving dependent details {0}", dependentDTO));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200, "success", "Dependent details successfully saved", Instant.now())));
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save dependent details. Error {0}", err.getMessage()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(400, "fail", "Failed to save dependent details", Instant.now())));
                });
    }

    Mono<ResponseEntity<APIResponse>> saveBeneficiary(BeneficiaryDTO beneficiaryDTO) {
        return lifeInsuranceRepository.saveBeneficiary(beneficiaryDTO.getPolicyNumber(), beneficiaryDTO.getClient(), beneficiaryDTO.getContactCell(), beneficiaryDTO.getContactWorkTell(), beneficiaryDTO.getContactHomeTell(), beneficiaryDTO.getContactFax(), beneficiaryDTO.getContactEmail(), beneficiaryDTO.getSessionUserID(), beneficiaryDTO.getPrefType(), beneficiaryDTO.getBenLastName(), beneficiaryDTO.getBenFirstName(), beneficiaryDTO.getBenIDNumber(), beneficiaryDTO.getBenPercentage(), AppUtil.formatDate(beneficiaryDTO.getBenDOB()), beneficiaryDTO.getBenRelation(), beneficiaryDTO.getBenType(), beneficiaryDTO.getTitle(), beneficiaryDTO.getMainMemberID())
                .then(Mono.just("next"))
                .flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed saving beneficiary details {0}", beneficiaryDTO.getBenIDNumber()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200, "success", "Beneficiary details successfully saved", Instant.now())));
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save beneficiary details. Error {0}", err.getMessage()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(400, "fail", "Failed to save beneficiary details", Instant.now())));
                });
    }

    Mono<ResponseEntity<APIResponse>> saveBankDetails(BankDetailsDTO bankDetailsDTO) {
        return lifeInsuranceRepository.saveBankDetails(bankDetailsDTO.getClient(), bankDetailsDTO.getPolicyNumber(), bankDetailsDTO.getSessionUserID(), bankDetailsDTO.getBranchCode(), bankDetailsDTO.getAccNumber(), bankDetailsDTO.getAccType(), bankDetailsDTO.getAccName(), bankDetailsDTO.getDedDay(), bankDetailsDTO.getFromDate(), bankDetailsDTO.getIdNumber(), bankDetailsDTO.getSubNaedo())
                .then(Mono.just("next"))
                .flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed saving bank details details {0}", bankDetailsDTO.getIdNumber()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200, "success", "Bank details successfully saved", Instant.now())));
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save bank details. Error {0}", err.getMessage()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(400, "fail", "Failed to save bank details details", Instant.now())));
                });
    }

    public Mono<APIResponse> getLifeInsuranceByUserId(String idNumber) {
        Flux<Member> memberRecordsFlux = lifeInsuranceRepository.findMemberByIdNumber(idNumber);
        return memberRecordsFlux.collectList().flatMap(memberList -> {
            List<Member> members = new ArrayList<>();
            return Flux.fromIterable(memberList).flatMap(member -> {
                        LOG.info(String.valueOf(member.getIdNumber()));
                        return getMemberByIdNumber(member.getIdNumber())
                                .flatMap(mem -> {
                                    members.add(mem);
                                    LOG.info("Adding member");
                                    return Mono.just("next");
                                });


                    }).then(Mono.just(new APIResponse(200, "success", members, Instant.now())))
                    .flatMap(apiRes -> {
                        return Mono.just(apiRes);
                    });
        }).onErrorResume(err -> {
            return Mono.just(new APIResponse(500, "Fail", "Request failed", Instant.now()));
        });
    }

    public Mono<Member> getMemberByIdNumber(String idNumber) {
        Member member = new Member();
        LOG.info(String.valueOf(idNumber));
        return lifeInsuranceRepository.findMemberByIdNumber(idNumber)
                .flatMap(mem -> {

                    member.setId(mem.getId());
                    member.setPol_360_main_member_id(mem.getPol_360_main_member_id());
                    member.setAge(mem.getAge());
                    member.setClient(mem.getClient());
                    member.setAgentCode(mem.getAgentCode());
                    member.setPolicyNumber(mem.getPolicyNumber());
                    member.setBrokerCode(mem.getBrokerCode());
                    member.setTitle(mem.getTitle());
                    member.setFirstName(mem.getFirstName());
                    member.setSurname(mem.getSurname());
                    member.setIdNumber(mem.getIdNumber());
                    member.setGender(mem.getGender());
                    member.setDateOfBirth(mem.getDateOfBirth());
                    member.setCellNumber(mem.getCellNumber());
                    member.setAltCellNumber(mem.getAltCellNumber());
                    member.setEmail(mem.getEmail());

                    member.setWorkNumber(mem.getWorkNumber());
                    member.setHomeNumber(mem.getHomeNumber());
                    member.setFax(mem.getFax());
                    member.setContactType(mem.getContactType());
                    member.setPostalAddress1(mem.getPostalAddress1());
                    member.setPostalAddress2(mem.getPostalAddress2());
                    member.setPostalAddress3(mem.getPostalAddress3());
                    member.setPostalCode(mem.getPostalCode());
                    member.setResidentialAddress1(mem.getResidentialAddress1());
                    member.setResidentialAddress2(mem.getResidentialAddress2());
                    member.setResidentialAddress3(mem.getResidentialAddress3());
                    member.setResidentialCode(mem.getResidentialCode());
                    member.setMemberType(mem.getMemberType());
                    member.setPremium(mem.getPremium());
                    member.setCover(mem.getCover());
                    member.setAddPolicyID(mem.getAddPolicyID());
                    member.setStatusCode(mem.getStatusCode());
                    member.setNameSpaceScanPass(mem.isNameSpaceScanPass());
                    member.setNameSpaceScanFailReason(mem.getNameSpaceScanFailReason());
                    member.setIdVerificationPass(mem.isIdVerificationPass());
                    member.setIdVerificationFailReason(mem.getIdVerificationFailReason());
                    member.setStep(mem.getStep());

                    return Mono.just(mem);
                })
                .then(Mono.just("Add spouses")).flatMap(allRisksStep -> {
                    LOG.info("Spouses");
                    return lifeInsuranceRepository.findSpouseByIdNumber(String.valueOf(member.getPol_360_main_member_id())).collectList()
                            .flatMap(spouses -> {
                                member.setSpouses(spouses);
                                return Mono.just(spouses);
                            });
                })

                .then(Mono.just("Add dependents")).flatMap(dependentsStep -> {
                    LOG.info("Dependents");
                    return lifeInsuranceRepository.findDependentByIdNumber(String.valueOf(member.getPol_360_main_member_id())).collectList()
                            .flatMap(dependents -> {
                                member.setDependents(dependents);
                                return Mono.just(dependents);
                            });
                })
                .then(Mono.just("Add extended members")).flatMap(allRisksStep -> {
                    LOG.info("Extended members");
                    return lifeInsuranceRepository.findExtendedMemberByIdNumber(String.valueOf(member.getPol_360_main_member_id())).collectList()
                            .flatMap(exDependents -> {
                                member.setExtendedMembers(exDependents);
                                return Mono.just(exDependents);
                            });
                }).then(Mono.just("Add beneficiaries")).flatMap(allRisksStep -> {
                    LOG.info("Beneficiaries");
                    return lifeInsuranceRepository.findBeneficiaryByIdNumber(String.valueOf(member.getPolicyNumber())).collectList()
                            .flatMap(beneficiaries -> {
                                member.setBeneficiaries(beneficiaries);
                                return Mono.just(beneficiaries);
                            });
                })
                .then(Mono.just("Add bank details")).flatMap(allRisksStep -> {
                    LOG.info("Bank Details");
                    return lifeInsuranceRepository.findBankDetailsByIdNumber(idNumber).collectList()
                            .flatMap(bankDetails -> {
                                member.setBankDetails(bankDetails);
                                return Mono.just(bankDetails);
                            });
                })
                .then(Mono.just("Add member")).flatMap(res -> {
                    LOG.info("Adding member");
                    return Mono.just(member);
                });
    }

    public Mono<ResponseEntity<APIResponse>> getMemberStep(String idNumber) {
        return lifeInsuranceRepository.findMemberLastRecordStepByIdNumber(idNumber)
                .flatMap(memberStep -> {
                    LOG.info(MessageFormat.format("Completed retrieving member step details {0}", idNumber));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200, "success", memberStep, Instant.now())));
                }).switchIfEmpty(Mono.defer(() -> {
                    LOG.error(MessageFormat.format("User {0} not found ", idNumber));
                    return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400, "Request failed", null, Instant.now())));
                }))
                .onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to retrieve step details. Error {0}", err.getMessage()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(400, "fail", null, Instant.now())));
                });
    }

    public Mono<ResponseEntity<APIResponse>> saveMemberDocument(String mainMemberID, String policyNumber, String clientName, String function, Mono<FilePart> filePartMono, String docType) {
        LOG.info(" Saving member document document");
        setConfigs(pol360EndpointUrl);

        return lifeInsuranceRepository.findMemberLastRecordByMainMemberNumber(Long.valueOf(mainMemberID)).flatMap(profile -> {
                    LOG.info(MessageFormat.format("Saving document for user with memberID {0}", mainMemberID));

                    return filePartMono.flatMap(filePart -> {
                                String newFileName = "";
                                LOG.info(MessageFormat.format("main memberId {0}", mainMemberID));
                                int splitLength = filePart.filename().split("\\.").length;
                                String fileExtension = filePart.filename().split("\\.")[splitLength - 1];

                                newFileName = profile.getIdNumber() + "_" + DOCType.getDocType(docType) + "." + fileExtension;

                                MultipartBodyBuilder formData = new MultipartBodyBuilder();
                                formData.part("MemberID", mainMemberID);
                                formData.part("PolicyNumber", policyNumber);
                                formData.part("ClientName", clientName);
                                formData.part("Function", function);
                                formData.part("DocType", docType);
                                formData.part("File", filePart);

                                return filePart.transferTo(Paths.get(fileUploadDir + newFileName))
                                        .then(Mono.just(newFileName).flatMap(fileName -> {
                                            LOG.info(MessageFormat.format("Saving {0} for userId {1} with path {2}", docType, mainMemberID, fileUploadDir + fileName));
                                            return saveMainMemberDocumentDetails(mainMemberID, docType, fileUploadDir + fileName);
                                        })).then(Mono.just("next step")
                                                .flatMap(msg -> {
                                                    return uploadDocToPol360(formData)
                                                            .flatMap(apiResponse -> {
                                                                if (apiResponse.getStatus() == 200) {
                                                                    return Mono.just(ResponseEntity.ok().body(apiResponse));
                                                                } else {
                                                                    return Mono.just(ResponseEntity.badRequest().body(apiResponse));
                                                                }
                                                            });
                                                }));
                            })
                            .onErrorResume(error -> {
                                LOG.error(MessageFormat.format("Error uploading {0}  for user with member id {1} {2}", docType, mainMemberID, error.getMessage()));
                                return Mono.just(ResponseEntity.internalServerError().body(new APIResponse(500, "Request failed", "Error uploading " + docType + " for user with main member id" + mainMemberID + " " + error.getMessage(), Instant.now())));

                            });
                }).onErrorResume(error -> {
                    LOG.error(MessageFormat.format("Error checking user with member id {0} {1} ", mainMemberID, error.getMessage()));
                    return Mono.just(ResponseEntity.internalServerError().body(new APIResponse(500, "Request failed", "Error checking user with member id " + mainMemberID + " not found" + error.getMessage(), Instant.now())));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    LOG.error(MessageFormat.format("User with memberId {0} not found", mainMemberID));
                    return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400, "Request failed", "User member Id " + mainMemberID + " not found", Instant.now())));
                }));

    }

    public Mono<APIResponse> saveMainMemberDocumentDetails(String mainMemberId, String docType, String filePath) {

        return lifeInsuranceRepository.updateMainMemberDocumentsDetails(docType, filePath, mainMemberId).then(Mono.just("NEXT")).flatMap(cust -> {
            LOG.info(MessageFormat.format("User with main member Id: {0} {1} documents successfully saved", mainMemberId, docType));
            return Mono.just(new APIResponse(200, "success", "Document successfully saved", Instant.now()));
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("User with main member Id: {0} {1} could not be saved. Error {2}", mainMemberId, docType, err.getMessage()));
            return Mono.just(new APIResponse(500, "Request failed", "User with main member id: " + mainMemberId + " " + docType + " couldn't be saved", Instant.now()));
        });
    }

    public Mono<APIResponse> uploadDocToPol360(MultipartBodyBuilder builder) {

        return tokenService.getPol360APIToken().flatMap(
                bearerToken -> {
                    if (!bearerToken.isEmpty()) {
                        return webClient.post()
                                .uri("/api/360API.php")
                                .header(HttpHeaders.ACCEPT, "*/*")
                                .headers((headers -> headers.add("Authorization", bearerToken)))
                                .body(BodyInserters.fromMultipartData(builder.build()))
                                .retrieve()
                                .toEntity(APIResponse.class).map(responseEntity -> {
                                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                                        APIResponse apiResponse = responseEntity.getBody();

                                        return new APIResponse(200, "success", apiResponse.getMessage(), Instant.now());
                                    } else {
                                        LOG.error("Error uploading member file to pol360");
                                        return new APIResponse(400, "fail", "Error uploading member file to pol360", Instant.now());
                                    }
                                }).onErrorResume(error -> {
                                    LOG.error(MessageFormat.format("Failed to retrieve products {0}", error.getMessage()));
                                    return Mono.just(new APIResponse(400, "fail", "Failed retrieve products ", Instant.now()));
                                });
                    } else {
                        return Mono.just(new APIResponse(400, "fail", "Error retrieving products", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            return Mono.just(apiResponse);
        });
    }

    public String generatePolicyId(String strPolicyId) {
        Long policyId = AppUtil.stringToLong(strPolicyId);
        if (policyId == -1L) {
            policyId = policyId + 100000;
        } else {
            policyId = policyId + 1;
        }
        return String.valueOf(policyId);
    }

    Mono<String> sendEmailLifeCoverNotification(String idNumber) {
        return lifeInsuranceRepository.findMemberLastRecordByIdNumber(idNumber)
                .flatMap(member -> {
                    return emailService.sendEmailForLifeCover(member, "New eSure Request To Create A Life Cover Account").flatMap(Mono::just);
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to send email life cover ref {0}. Error {1}", idNumber, err.getMessage()));
                    return Mono.just("Failed to send email");
                });
    }
}
