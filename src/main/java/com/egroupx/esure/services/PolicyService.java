package com.egroupx.esure.services;

import com.egroupx.esure.dto.fsp_policy.Policy;

import com.egroupx.esure.model.policies.PolicyAdditionalInfo;
import com.egroupx.esure.model.responses.api.APIResponse;

import com.egroupx.esure.model.responses.fsp_qoute_policies.PolicyResponse;
import com.egroupx.esure.repository.CustomerRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;


@Service
public class PolicyService {

    @Value("${egroupx.services.fsp.endpointUrl}")
    private String fspEndpointUrl;

    @Value("${egroupx.services.fsp.apiKey:}")
    private String fspAPIKey;

    @Value("${egroupx.email.sendEmail}")
    private String sendEmail;

    private WebClient webClient;

    private final Logger LOG = LoggerFactory.getLogger(PolicyService.class);

    private final CustomerRepository customerRepository;

    private final QuotationService quotationService;

    private final EmailService emailService;

    public PolicyService(CustomerRepository customerRepository, CustomerService customerService, QuotationService quotationService, EmailService emailService) {
        this.customerRepository = customerRepository;
        this.quotationService = quotationService;
        this.emailService = emailService;
    }

    private void setConfigs(String endpointUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(endpointUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((fspAPIKey + ":" + "").getBytes()))
                .build();
    }

    public Mono<ResponseEntity<APIResponse>> acceptPolicy(Long policyId, Policy policyReq) {

        setConfigs(fspEndpointUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        String formattedPolicyReq = null;
        try {
            formattedPolicyReq = objectMapper.writeValueAsString(policyReq);
        } catch (JsonProcessingException ex) {
            return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400, "Failed", "Failed to get process request", Instant.now())));
        }
        return webClient.put()
                .uri("/api/insure/policies/" + policyId + "/accept")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, "*/*")
                .body(BodyInserters.fromObject(formattedPolicyReq))
                .retrieve()
                .toEntity(Object.class).map(responseEntity -> {
                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                        Object obj2 = responseEntity.getBody();
                        LOG.info("Successfully processed policy");
                        // return
                        return new APIResponse(200, "success", obj2, Instant.now());
                    } else {
                        LOG.error(MessageFormat.format("Failed to process policy. Error code {0}", responseEntity.getStatusCode().value()));
                        return new APIResponse(400, "Failed to process policy", "Failed to process policy. Please try again or contact admin", Instant.now());
                    }
                }).onErrorResume(error -> {
                    LOG.error(MessageFormat.format("Failed to process policy. Error code {0}", error.getMessage()));
                    return Mono.just(new APIResponse(500, "Failed to process policy", "Failed to process policy. Please try again or contact admin", Instant.now()));
                }).flatMap(apiResponse -> {
                    if (apiResponse.getStatus() == 200) {
                        return retrieveAndSavePolicy(policyId, "success", policyReq).flatMap(polRes -> {
                            return sendEmailNotifcation(policyId, policyReq).then(Mono.just("next"))
                                    .flatMap(msg -> Mono.just(polRes));
                        });
                    } else {
                        return retrieveAndSavePolicy(policyId, "FSP_Accept_Policy_Error", policyReq).flatMap(polRes -> {
                            //send email
                            return sendEmailNotifcation(policyId, policyReq).then(Mono.just("next"))
                                    .flatMap(msg -> Mono.just(polRes));
                        });
                    }
                });
    }

    public Mono<PolicyResponse> getFSPPolicy(Long policyId) {

        setConfigs(fspEndpointUrl);
        return webClient.get()
                .uri("/api/insure/policies/" + policyId)
                .header(HttpHeaders.ACCEPT, "*/*")
                .retrieve()
                .toEntity(PolicyResponse.class).map(responseEntity -> {
                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                        PolicyResponse policy = responseEntity.getBody();
                        LOG.info("Successfully retrieved a policy");
                        return policy;
                    } else {
                        LOG.error(MessageFormat.format("Failed to retrieve a policy. Error code {0}", responseEntity.getStatusCode().value()));
                        return null;
                    }
                }).onErrorResume(error -> {
                    LOG.error(MessageFormat.format("Failed to retrieve a policy. Error code {0}", error.getMessage()));
                    return null;
                });
    }

    public Mono<ResponseEntity<APIResponse>> getPolicyByUserId(String idNumber) {

        ArrayList<com.egroupx.esure.model.policies.Policy> policyList = new ArrayList<>();
        return customerRepository.getPoliciesByIdNumber(idNumber).collectList().flatMap(policies -> {
            LOG.info("Completed retrieving policy details by id number");

            return Flux.fromIterable(policies).flatMap(plcy -> {
                        com.egroupx.esure.model.policies.Policy policy = new com.egroupx.esure.model.policies.Policy();
                        policy.setId(plcy.getId());
                        policy.setFspPolicyId(plcy.getFspPolicyId());
                        policy.setInsurerId(plcy.getInsurerId());
                        policy.setCategoryId(plcy.getCategoryId());
                        policy.setDateQuoted(plcy.getDateQuoted());
                        policy.setStatus(plcy.getStatus());
                        policy.setIdNumber(plcy.getIdNumber());
                        policy.setBrokerCode(plcy.getBrokerCode());
                        policy.setExternalPolicyNo(plcy.getExternalPolicyNo());
                        policy.setQuotationId(plcy.getQuotationId());
                        policy.setQuotedPremium(plcy.getQuotedPremium());
                        policy.setOfferingId(plcy.getOfferingId());
                        policy.setOfferingName(plcy.getOfferingName());
                        policy.setErrorStatus(plcy.getErrorStatus());
                        policy.setESureStatus(plcy.getESureStatus());

                        return quotationService.getQuotationByFSPQouteRef(plcy.getQuotationId()).flatMap(quotation -> {
                                    policy.setPolicyHolders(quotation.getPolicyHolders());
                                    policy.setAllRisks(quotation.getAllRisks());
                                    policy.setBuildings(quotation.getBuildings());
                                    policy.setHouseholdContents(quotation.getHouseholdContents());
                                    policy.setMotorVehicles(quotation.getMotorVehicles());

                                    return Mono.just(quotation);
                                }).then(Mono.just("policy additional info").flatMap(pol -> {
                                    return getPolicyAdditionalInfo(plcy.getQuotationId()).flatMap(pInfo->{
                                        policy.setAdditionalInfo(pInfo);
                                        return Mono.just(pInfo);
                                    });
                                }))
                                .then(Mono.just("add qoute").flatMap(pol -> {
                                    policyList.add(policy);
                                    return Mono.just(policyList);
                                }));

                    })
                    .then(Mono.just("add qoute").flatMap(pol -> {
                        return Mono.just(policyList);
                    }));
        }).then(Mono.just("next")).flatMap(msg -> {
            return Mono.just(ResponseEntity.ok().body(new APIResponse(200, "success", policyList, Instant.now())));
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save policy details. Error {1}", err.getMessage()));
            return Mono.just(ResponseEntity.badRequest().body(new APIResponse(200, "success", policyList, Instant.now())));
        });
    }

    public Mono<ResponseEntity<APIResponse>> retrieveAndSavePolicy(Long policyId, String esureStatus, Policy policyReq) {
        return getFSPPolicy(policyId).flatMap(policy -> {
            String idNumber = "";
            if (policy != null) {
                if (policy.getPolicyHolders() != null && policy.getPolicyHolders().length > 0 && policy.getPolicyHolders()[0].getPerson() != null) {
                    idNumber = policy.getPolicyHolders()[0].getPerson().getIdNumber();
                }
                if(idNumber==null || idNumber.isEmpty()){
                    if (policy.getPolicyHolders() != null && policy.getPolicyHolders().length > 0 && policy.getPolicyHolders()[0].getPerson() != null){
                        idNumber = policy.getPolicyHolders()[0].getPerson().getPassportNumber();
                    }
                }

                return savePolicy(policyId, AppUtil.stringToLong(policy.getInsurerId()), AppUtil.stringToInteger(policy.getCategoryId()), policy.getDateQuoted(), "ACCEPTED", AppUtil.stringToLong(policy.getBrokerCode()), policy.getExternalPolicyNo(), AppUtil.stringToLong(policy.getQuotationId()), AppUtil.stringToDouble(policy.getQuotePremium()), AppUtil.stringToLong(policy.getOfferingId()), policy.getOfferingName(), policy.getErrorStatus(), idNumber, esureStatus, AppUtil.stringToLong(policy.getQuotationId()), policyReq).
                        flatMap(msg -> {
                            LOG.info(msg);
                            return Mono.just(ResponseEntity.ok().body(new APIResponse(200, "success", msg, Instant.now())));
                        }).onErrorResume(error -> {
                            LOG.error("Error " + error.getMessage());
                            return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400, "fail", "Something went wrong", Instant.now())));
                        });

            } else {
                return Mono.just(ResponseEntity.internalServerError().body(new APIResponse(500, "fail", "Policy could not be processed", Instant.now())));
            }
        }).onErrorResume(error -> {
            LOG.error("Failed to process qoute " + error.getMessage());
            return Mono.just(ResponseEntity.internalServerError().body(new APIResponse(500, "fail", "Policy could not be processed", Instant.now())));
        });

    }


   /* public Mono<ResponseEntity<APIResponse>> getPolicyById(Long policyId) {
        return customerRepository.(policyId).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed retrieving policy details {0} for policy id",policyId));
            return Mono.just("Completed retrieving policy details for policy id "+ policyId);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to retrieve policy details for policy id {0}. Error {1}",policyId,err.getMessage()));
            return Mono.just("Failed to retrieve policy details");
        });
    }*/


    Mono<String> savePolicy(Long fspPolicyId, Long insurerId, int categoryId, LocalDateTime dateQuoted, String status, Long brokerCode, String externalPolicyNo, Long quotationId, double quotedPremium, Long offeringId, String offeringName, String errorStatus, String idNumber, String eSureStatus, Long fspQouteRef, Policy policyReq) {
        return customerRepository.savePolicy(fspPolicyId, insurerId, categoryId, dateQuoted, status, brokerCode, externalPolicyNo, quotationId, quotedPremium, offeringId, offeringName, errorStatus, idNumber, eSureStatus)
                .then(Mono.just("next")).flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed saving policy details {0} for quote ref", fspQouteRef));
                    return savePolicyAdditionalInfo(quotationId, fspPolicyId, policyReq);
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save policy details for quote ref {0}. Error {1}", fspQouteRef, err.getMessage()));
                    return Mono.just("Failed to save policy details");
                });
    }

    Mono<String> savePolicyAdditionalInfo(Long fspQuoteRef, Long fspPolicyId, Policy policyReq) {
        String contact = "";
        String instructions = "";
        if (policyReq != null && policyReq.getPolicyHolders().length > 0 && policyReq.getPolicyHolders()[0].getAddresses().length > 0) {
            contact = policyReq.getPolicyHolders()[0].getAddresses()[0].getCode() + policyReq.getPolicyHolders()[0].getAddresses()[0].getNumber();
        }
        if (policyReq.getInstructions() != null && policyReq.getInstructions().length > 0) {
            for (String ins : policyReq.getInstructions()) {
                instructions = instructions.concat(ins);
            }
        }
        return customerRepository.savePolicyAdditionalInfo(fspQuoteRef, fspPolicyId, contact, instructions)
                .then(Mono.just("next")).flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed saving policy info details {0} for quote ref", fspQuoteRef));
                    return Mono.just("Completed saving policy info details for quote ref " + fspQuoteRef);
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save policy info details for quote ref {0}. Error {1}", fspQuoteRef, err.getMessage()));
                    return Mono.just("Failed to save policy info details");
                });
    }

    Mono<PolicyAdditionalInfo> getPolicyAdditionalInfo(Long fspQuoteRef) {
        return customerRepository.getPolicyAdditionalInfo(fspQuoteRef).flatMap(Mono::just).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to retrieve policy additional info by ref id. Error {0}", err.getMessage()));
            return Mono.empty();
        });
    }

    Mono<String> sendEmailNotifcation(Long fspPolicyId, Policy policyReq) {
        return customerRepository.getCustomerEmailDetailsByPolicyId(fspPolicyId)
                .flatMap(customer -> {
                    String instructions = "";
                    if (policyReq != null && policyReq.getPolicyHolders().length > 0 && policyReq.getPolicyHolders()[0].getAddresses().length > 0) {

                        customer.setCode(policyReq.getPolicyHolders()[0].getAddresses()[0].getCode());
                        customer.setNumber(policyReq.getPolicyHolders()[0].getAddresses()[0].getNumber());
                    }
                    if (policyReq.getInstructions() != null && policyReq.getInstructions().length > 0) {
                        for (String ins : policyReq.getInstructions()) {
                            instructions = instructions.concat(ins);
                        }
                        customer.setInstructions(instructions);
                    }
                    return emailService.sendEmail(customer, "New Esure Policy")
                            .flatMap(email-> emailService.sendInsuranceWelcomeEmail(customer,"Welcome To eSure Insurance")
                                        .flatMap(Mono::just)
                            );
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to send email policy ref {0}. Error {1}", fspPolicyId, err.getMessage()));
                    return Mono.just("Failed to send email");
                });
    }


}
