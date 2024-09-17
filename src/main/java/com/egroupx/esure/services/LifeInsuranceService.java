package com.egroupx.esure.services;

import com.egroupx.esure.model.life.*;
import com.egroupx.esure.model.responses.api.APIResponse;
import com.egroupx.esure.repository.LifeInsuranceRepository;
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
import java.time.LocalDate;

@Service
public class LifeInsuranceService {

    @Value("${egroupx.services.fspEndpointUrl}")
    private String fspEndpointUrl;

    @Value("${egroupx.services.fspAPIKey:}")
    private String fspAPIKey;

    @Value("${egroupx.services.pol360EndpointUrl}")
    private String pol360EndpointUrl;

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
                bearerToken ->{
                    if(!bearerToken.isEmpty()){
                      return webClient.post()
                                .uri("/api/360APITEST.php/api/360API.php")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.ACCEPT, "*/*")
                                .headers((headers -> headers.add("authorization", "Bearer " + bearerToken)))
                                .bodyValue(memberDTO)
                                .retrieve()
                                .toEntity(Object.class).map(responseEntity -> {
                                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                                        Object resObj = responseEntity.getBody();
                                        //CitizenResponse citizenRes = responseEntity.getBody();
                                       /* if (citizenRes != null && citizenRes.getStatus() == 200) {
                                            LOG.info(MessageFormat.format("User ID Verified. Response status {0} details status is ", citizenRes.getStatus(),citizenRes.getResponse().getStatus()));
                                            return new APIResponse(200, "success", "User ID Verified. Response status "  + citizenRes.getStatus() + "details status detail is "+citizenRes.getResponse().getStatus(), Instant.now());
                                            //}
                                        } else {
                                            LOG.error(MessageFormat.format("User ID verification failed. Response status {0}", citizenRes.getStatus()));
                                            return new APIResponse(400, "fail", "User ID verification failed. Response status " + citizenRes.getStatus(), Instant.now());
                                        }*/
                                        return new APIResponse(200, "success", resObj, Instant.now());
                                    } else {
                                        LOG.error(MessageFormat.format("Failed to create member. Response status {0}", responseEntity.getStatusCode().value()));
                                        return new APIResponse(400, "fail", "Failed to create member. Response status " + responseEntity.getStatusCode().value(), Instant.now());
                                    }
                                }).onErrorResume(error -> {
                                    LOG.error(MessageFormat.format("Failed to create member {0}", error.getMessage()));
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create member ", Instant.now()));
                                });
                    }else{
                        return Mono.just(new APIResponse(400, "fail", "Error creating member", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
                    if(apiResponse.getStatus()==200) {
                        return saveMember("", memberDTO.getClient(), memberDTO.getAgentCode(), memberDTO.getPolicyNumber(), memberDTO.getBrokerCode(), memberDTO.getTitle(), memberDTO.getFirstName(), memberDTO.getSurname(), memberDTO.getIdNumber(),memberDTO.getGender(),memberDTO.getDateOfBirth(),
                                memberDTO.getAge(),memberDTO.getCellNumber(),memberDTO.getAltCellNumber(),memberDTO.getWorkNumber(),memberDTO.getHomeNumber(),memberDTO.getEmail(),memberDTO.getFax(), memberDTO.getContactType(), memberDTO.getPostalAddress1(), memberDTO.getPostalAddress2(), memberDTO.getPostalAddress3(), memberDTO.getPostalCode(), memberDTO.getResidentialAddress1(),
                                memberDTO.getResidentialAddress2(), memberDTO.getResidentialAddress3(), memberDTO.getResidentialCode(), memberDTO.getMemberType(), memberDTO.getPremium(), memberDTO.getCover(), memberDTO.getAddPolicyID(),memberDTO.getStatusCode());
                    }else{
                        return saveMember("", memberDTO.getClient(), memberDTO.getAgentCode(), memberDTO.getPolicyNumber(), memberDTO.getBrokerCode(), memberDTO.getTitle(), memberDTO.getFirstName(), memberDTO.getSurname(), memberDTO.getIdNumber(),memberDTO.getGender(),memberDTO.getDateOfBirth(),
                                memberDTO.getAge(),memberDTO.getCellNumber(),memberDTO.getAltCellNumber(),memberDTO.getWorkNumber(),memberDTO.getHomeNumber(),memberDTO.getEmail(),memberDTO.getFax(), memberDTO.getContactType(), memberDTO.getPostalAddress1(), memberDTO.getPostalAddress2(), memberDTO.getPostalAddress3(), memberDTO.getPostalCode(), memberDTO.getResidentialAddress1(),
                                memberDTO.getResidentialAddress2(), memberDTO.getResidentialAddress3(), memberDTO.getResidentialCode(), memberDTO.getMemberType(), memberDTO.getPremium(), memberDTO.getCover(), memberDTO.getAddPolicyID(),memberDTO.getStatusCode());
                    }
        });
    }

    public Mono<ResponseEntity<APIResponse>> createBeneficiary(Beneficiary beneficiaryDTO) {
        setConfigs(pol360EndpointUrl);

        return tokenService.getPol360APIToken().flatMap(
                bearerToken ->{
                    if(!bearerToken.isEmpty()){
                        return webClient.post()
                                .uri("/api/360APITEST.php/api/360API.php")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.ACCEPT, "*/*")
                                .headers((headers -> headers.add("authorization", "Bearer " + bearerToken)))
                                .bodyValue(beneficiaryDTO)
                                .retrieve()
                                .toEntity(Object.class).map(responseEntity -> {
                                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                                        Object resObj = responseEntity.getBody();
                                        //CitizenResponse citizenRes = responseEntity.getBody();
                                       /* if (citizenRes != null && citizenRes.getStatus() == 200) {
                                            LOG.info(MessageFormat.format("User ID Verified. Response status {0} details status is ", citizenRes.getStatus(),citizenRes.getResponse().getStatus()));
                                            return new APIResponse(200, "success", "User ID Verified. Response status "  + citizenRes.getStatus() + "details status detail is "+citizenRes.getResponse().getStatus(), Instant.now());
                                            //}
                                        } else {
                                            LOG.error(MessageFormat.format("User ID verification failed. Response status {0}", citizenRes.getStatus()));
                                            return new APIResponse(400, "fail", "User ID verification failed. Response status " + citizenRes.getStatus(), Instant.now());
                                        }*/
                                        return new APIResponse(200, "success", resObj, Instant.now());
                                    } else {
                                        LOG.error(MessageFormat.format("Failed to create member. Response status {0}", responseEntity.getStatusCode().value()));
                                        return new APIResponse(400, "fail", "Failed to create member. Response status " + responseEntity.getStatusCode().value(), Instant.now());
                                    }
                                }).onErrorResume(error -> {
                                    LOG.error(MessageFormat.format("Failed to create member {0}", error.getMessage()));
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create member ", Instant.now()));
                                });
                    }else{
                        return Mono.just(new APIResponse(400, "fail", "Error creating member", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if (apiResponse.getStatus() == 200) {
                return createBeneficiary(beneficiaryDTO);
            } else {
                return createBeneficiary(beneficiaryDTO);
            }
        });
    }

    public Mono<ResponseEntity<APIResponse>> createDependent(Dependent dependentDTO) {
        setConfigs(pol360EndpointUrl);

        return tokenService.getPol360APIToken().flatMap(
                bearerToken ->{
                    if(!bearerToken.isEmpty()){
                        return webClient.post()
                                .uri("/api/360APITEST.php/api/360API.php")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.ACCEPT, "*/*")
                                .headers((headers -> headers.add("authorization", "Bearer " + bearerToken)))
                                .bodyValue(dependentDTO)
                                .retrieve()
                                .toEntity(Object.class).map(responseEntity -> {
                                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                                        Object resObj = responseEntity.getBody();
                                        //CitizenResponse citizenRes = responseEntity.getBody();
                                       /* if (citizenRes != null && citizenRes.getStatus() == 200) {
                                            LOG.info(MessageFormat.format("User ID Verified. Response status {0} details status is ", citizenRes.getStatus(),citizenRes.getResponse().getStatus()));
                                            return new APIResponse(200, "success", "User ID Verified. Response status "  + citizenRes.getStatus() + "details status detail is "+citizenRes.getResponse().getStatus(), Instant.now());
                                            //}
                                        } else {
                                            LOG.error(MessageFormat.format("User ID verification failed. Response status {0}", citizenRes.getStatus()));
                                            return new APIResponse(400, "fail", "User ID verification failed. Response status " + citizenRes.getStatus(), Instant.now());
                                        }*/
                                        return new APIResponse(200, "success", resObj, Instant.now());
                                    } else {
                                        LOG.error(MessageFormat.format("Failed to create member. Response status {0}", responseEntity.getStatusCode().value()));
                                        return new APIResponse(400, "fail", "Failed to create member. Response status " + responseEntity.getStatusCode().value(), Instant.now());
                                    }
                                }).onErrorResume(error -> {
                                    LOG.error(MessageFormat.format("Failed to create member {0}", error.getMessage()));
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create member ", Instant.now()));
                                });
                    }else{
                        return Mono.just(new APIResponse(400, "fail", "Error creating member", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if(apiResponse.getStatus()==200) {
                return createDependent(dependentDTO);
            }else{
                return createDependent(dependentDTO);
        }
        });
    }

    public Mono<ResponseEntity<APIResponse>> createExtendedMember(ExtendedMember extendedMemberDTO) {
        setConfigs(pol360EndpointUrl);

        return tokenService.getPol360APIToken().flatMap(
                bearerToken ->{
                    if(!bearerToken.isEmpty()){
                        return webClient.post()
                                .uri("/api/360APITEST.php/api/360API.php")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.ACCEPT, "*/*")
                                .headers((headers -> headers.add("authorization", "Bearer " + bearerToken)))
                                .bodyValue(extendedMemberDTO)
                                .retrieve()
                                .toEntity(Object.class).map(responseEntity -> {
                                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                                        Object resObj = responseEntity.getBody();
                                        //CitizenResponse citizenRes = responseEntity.getBody();
                                       /* if (citizenRes != null && citizenRes.getStatus() == 200) {
                                            LOG.info(MessageFormat.format("User ID Verified. Response status {0} details status is ", citizenRes.getStatus(),citizenRes.getResponse().getStatus()));
                                            return new APIResponse(200, "success", "User ID Verified. Response status "  + citizenRes.getStatus() + "details status detail is "+citizenRes.getResponse().getStatus(), Instant.now());
                                            //}
                                        } else {
                                            LOG.error(MessageFormat.format("User ID verification failed. Response status {0}", citizenRes.getStatus()));
                                            return new APIResponse(400, "fail", "User ID verification failed. Response status " + citizenRes.getStatus(), Instant.now());
                                        }*/
                                        return new APIResponse(200, "success", resObj, Instant.now());
                                    } else {
                                        LOG.error(MessageFormat.format("Failed to create member. Response status {0}", responseEntity.getStatusCode().value()));
                                        return new APIResponse(400, "fail", "Failed to create member. Response status " + responseEntity.getStatusCode().value(), Instant.now());
                                    }
                                }).onErrorResume(error -> {
                                    LOG.error(MessageFormat.format("Failed to create member {0}", error.getMessage()));
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create member ", Instant.now()));
                                });
                    }else{
                        return Mono.just(new APIResponse(400, "fail", "Error creating member", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if(apiResponse.getStatus()==200) {
                return saveExtendedDependent(extendedMemberDTO.getMainMemberID(),extendedMemberDTO.getTitle(), extendedMemberDTO.getFirstName(), extendedMemberDTO.getSurname(), extendedMemberDTO.getIdNumber(), extendedMemberDTO.getGender(), extendedMemberDTO.getDateOfBirth(),extendedMemberDTO.getAge(), extendedMemberDTO.getMainMemberID(), extendedMemberDTO.getPolicyNumber());
            }else{
                return saveExtendedDependent(extendedMemberDTO.getMainMemberID(),extendedMemberDTO.getTitle(), extendedMemberDTO.getFirstName(), extendedMemberDTO.getSurname(), extendedMemberDTO.getIdNumber(), extendedMemberDTO.getGender(), extendedMemberDTO.getDateOfBirth(),extendedMemberDTO.getAge(), extendedMemberDTO.getMainMemberID(), extendedMemberDTO.getPolicyNumber());
            }
        });
    }

    public Mono<ResponseEntity<APIResponse>> createSpouse(Spouse spouseDTO) {
        setConfigs(pol360EndpointUrl);

        return tokenService.getPol360APIToken().flatMap(
                bearerToken ->{
                    if(!bearerToken.isEmpty()){
                        return webClient.post()
                                .uri("/api/360APITEST.php/api/360API.php")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.ACCEPT, "*/*")
                                .headers((headers -> headers.add("authorization", "Bearer " + bearerToken)))
                                .bodyValue(spouseDTO)
                                .retrieve()
                                .toEntity(Object.class).map(responseEntity -> {
                                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                                        Object resObj = responseEntity.getBody();
                                        //CitizenResponse citizenRes = responseEntity.getBody();
                                       /* if (citizenRes != null && citizenRes.getStatus() == 200) {
                                            LOG.info(MessageFormat.format("User ID Verified. Response status {0} details status is ", citizenRes.getStatus(),citizenRes.getResponse().getStatus()));
                                            return new APIResponse(200, "success", "User ID Verified. Response status "  + citizenRes.getStatus() + "details status detail is "+citizenRes.getResponse().getStatus(), Instant.now());
                                            //}
                                        } else {
                                            LOG.error(MessageFormat.format("User ID verification failed. Response status {0}", citizenRes.getStatus()));
                                            return new APIResponse(400, "fail", "User ID verification failed. Response status " + citizenRes.getStatus(), Instant.now());
                                        }*/
                                        return new APIResponse(200, "success", resObj, Instant.now());
                                    } else {
                                        LOG.error(MessageFormat.format("Failed to create member. Response status {0}", responseEntity.getStatusCode().value()));
                                        return new APIResponse(400, "fail", "Failed to create member. Response status " + responseEntity.getStatusCode().value(), Instant.now());
                                    }
                                }).onErrorResume(error -> {
                                    LOG.error(MessageFormat.format("Failed to create member {0}", error.getMessage()));
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create member ", Instant.now()));
                                });
                    }else{
                        return Mono.just(new APIResponse(400, "fail", "Error creating member", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if(apiResponse.getStatus()==200) {
                return saveSpouse(spouseDTO.getClient(),spouseDTO.getTitle(),spouseDTO.getSurname(), spouseDTO.getSurname(), spouseDTO.getIdNumber(), spouseDTO.getGender(), spouseDTO.getDateOfBirth(),spouseDTO.getAge(), spouseDTO.getMainMemberID(), spouseDTO.getPolicyNumber());
            }else{
                return saveSpouse(spouseDTO.getClient(),spouseDTO.getTitle(),spouseDTO.getSurname(), spouseDTO.getSurname(), spouseDTO.getIdNumber(), spouseDTO.getGender(), spouseDTO.getDateOfBirth(),spouseDTO.getAge(), spouseDTO.getMainMemberID(), spouseDTO.getPolicyNumber());
            }
        });
    }

    public Mono<ResponseEntity<APIResponse>> addBankDetails(BankDetails bankDetailsDTO) {
        setConfigs(pol360EndpointUrl);

        return tokenService.getPol360APIToken().flatMap(
                bearerToken ->{
                    if(!bearerToken.isEmpty()){
                        return webClient.post()
                                .uri("/api/360APITEST.php/api/360API.php")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.ACCEPT, "*/*")
                                .headers((headers -> headers.add("authorization", "Bearer " + bearerToken)))
                                .bodyValue(bankDetailsDTO)
                                .retrieve()
                                .toEntity(Object.class).map(responseEntity -> {
                                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                                        Object resObj = responseEntity.getBody();
                                        //CitizenResponse citizenRes = responseEntity.getBody();
                                       /* if (citizenRes != null && citizenRes.getStatus() == 200) {
                                            LOG.info(MessageFormat.format("User ID Verified. Response status {0} details status is ", citizenRes.getStatus(),citizenRes.getResponse().getStatus()));
                                            return new APIResponse(200, "success", "User ID Verified. Response status "  + citizenRes.getStatus() + "details status detail is "+citizenRes.getResponse().getStatus(), Instant.now());
                                            //}
                                        } else {
                                            LOG.error(MessageFormat.format("User ID verification failed. Response status {0}", citizenRes.getStatus()));
                                            return new APIResponse(400, "fail", "User ID verification failed. Response status " + citizenRes.getStatus(), Instant.now());
                                        }*/
                                        return new APIResponse(200, "success", resObj, Instant.now());
                                    } else {
                                        LOG.error(MessageFormat.format("Failed to create member. Response status {0}", responseEntity.getStatusCode().value()));
                                        return new APIResponse(400, "fail", "Failed to create member. Response status " + responseEntity.getStatusCode().value(), Instant.now());
                                    }
                                }).onErrorResume(error -> {
                                    LOG.error(MessageFormat.format("Failed to create member {0}", error.getMessage()));
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create member ", Instant.now()));
                                });
                    }else{
                        return Mono.just(new APIResponse(400, "fail", "Error creating member", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
            if(apiResponse.getStatus()==200) {
                return saveBankDetails(bankDetailsDTO.getClient(), bankDetailsDTO.getPolicyNumber(), bankDetailsDTO.getSessionUserID(), bankDetailsDTO.getBranchCode(), bankDetailsDTO.getAccNumber(), bankDetailsDTO.getAccType(), bankDetailsDTO.getAccName(), bankDetailsDTO.getDedDay(), bankDetailsDTO.getFromDate(), bankDetailsDTO.getIdNumber(), bankDetailsDTO.getSubNaedo());
            }else{
                return saveBankDetails(bankDetailsDTO.getClient(), bankDetailsDTO.getPolicyNumber(), bankDetailsDTO.getSessionUserID(), bankDetailsDTO.getBranchCode(), bankDetailsDTO.getAccNumber(), bankDetailsDTO.getAccType(), bankDetailsDTO.getAccName(), bankDetailsDTO.getDedDay(), bankDetailsDTO.getFromDate(), bankDetailsDTO.getIdNumber(), bankDetailsDTO.getSubNaedo());
            }
        });
    }

    public Mono<ResponseEntity<APIResponse>> getAllProducts(String clientName) {
        setConfigs(pol360EndpointUrl);

        return tokenService.getPol360APIToken().flatMap(
                bearerToken ->{
                    if(!bearerToken.isEmpty()){
                        return webClient.get()
                                .uri("/api/ProductAPI.php?ClientName="+clientName+"&Function=GetAllProducts")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.ACCEPT, "*/*")
                                .headers((headers -> headers.add("authorization", "Bearer " + bearerToken)))
                                .retrieve()
                                .toEntity(Object.class).map(responseEntity -> {
                                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                                        Object resObj = responseEntity.getBody();
                                        //CitizenResponse citizenRes = responseEntity.getBody();
                                       /* if (citizenRes != null && citizenRes.getStatus() == 200) {
                                            LOG.info(MessageFormat.format("User ID Verified. Response status {0} details status is ", citizenRes.getStatus(),citizenRes.getResponse().getStatus()));
                                            return new APIResponse(200, "success", "User ID Verified. Response status "  + citizenRes.getStatus() + "details status detail is "+citizenRes.getResponse().getStatus(), Instant.now());
                                            //}
                                        } else {
                                            LOG.error(MessageFormat.format("User ID verification failed. Response status {0}", citizenRes.getStatus()));
                                            return new APIResponse(400, "fail", "User ID verification failed. Response status " + citizenRes.getStatus(), Instant.now());
                                        }*/
                                        return new APIResponse(200, "success", resObj, Instant.now());
                                    } else {
                                        LOG.error(MessageFormat.format("Failed to create member. Response status {0}", responseEntity.getStatusCode().value()));
                                        return new APIResponse(400, "fail", "Failed to create member. Response status " + responseEntity.getStatusCode().value(), Instant.now());
                                    }
                                }).onErrorResume(error -> {
                                    LOG.error(MessageFormat.format("Failed to create member {0}", error.getMessage()));
                                    return Mono.just(new APIResponse(400, "fail", "Failed to create member ", Instant.now()));
                                });
                    }else{
                        return Mono.just(new APIResponse(400, "fail", "Error creating member", Instant.now()));
                    }
                }).flatMap(apiResponse -> {
                return Mono.just(ResponseEntity.ok().body(apiResponse));
        });
    }

    Mono<ResponseEntity<APIResponse>> saveMember(String pol360RefId, String client, String agentCode, String policyNumber, String brokerCode, String title, String firstName, String surname, String idNUmber, String gender, LocalDate dateOfBirth, String age, String cellNumber, String altCellNumber, String workNumber, String homeNumber, String email, String fax, String contactType, String postalAddress1, String postalAddress2, String postalAddress3, String postalCode, String residentialAddress1, String residentialAddress2, String residentialAddress3, String residentialCode, String memberType, String premium, String cover, String addPolicyId, String statusCode){
        return lifeInsuranceRepository.saveMember(pol360RefId,client,agentCode,policyNumber,brokerCode,title,firstName,surname,idNUmber,gender,dateOfBirth,age,cellNumber,altCellNumber,workNumber,homeNumber,email,fax,contactType,postalAddress1,postalAddress2,
                postalAddress3,postalCode,residentialAddress1,residentialAddress2,residentialAddress3,residentialCode,memberType,premium,cover,addPolicyId,statusCode
                ).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed saving member details {0}",pol360RefId));
            return Mono.just(ResponseEntity.ok().body(new APIResponse(200,"success","Spouse details successfully saved",Instant.now())));
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save member details. Error {0}",err.getMessage()));
            return Mono.just(ResponseEntity.ok().body(new APIResponse(400,"fail","Failed to save spouse details",Instant.now())));
        });
    }

    Mono<ResponseEntity<APIResponse>> saveSpouse(String client, String title, String firstName, String surname, String idNumber, String gender, LocalDate dateOfBirth, String age, String mainMemberId, String policyNumber){
            return lifeInsuranceRepository.saveSpouse(client,title,firstName,surname,idNumber,gender,dateOfBirth,age,mainMemberId,policyNumber).flatMap(msg->{
                LOG.info(MessageFormat.format("Completed saving spouse details {0}",mainMemberId));
                return Mono.just(ResponseEntity.ok().body(new APIResponse(200,"success","Spouse details successfully saved",Instant.now())));
            }).onErrorResume(err -> {
                LOG.error(MessageFormat.format("Failed to save spouse details. Error {0}",err.getMessage()));
                return Mono.just(ResponseEntity.ok().body(new APIResponse(400,"fail","Failed to save spouse details",Instant.now())));
            });

    }


    Mono<ResponseEntity<APIResponse>> saveExtendedDependent(String client, String title,String firstName,String surname,String idNumber,String gender,LocalDate dateOfBirth,String age,String mainMemberId,String policyNumber){
        return lifeInsuranceRepository.saveExtendedDependent(client,title,firstName,surname,idNumber,gender,dateOfBirth,age,mainMemberId,policyNumber).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed saving external dependent details {0}",mainMemberId));
            return Mono.just(ResponseEntity.ok().body(new APIResponse(200,"success","External member details successfully saved",Instant.now())));
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save external member details. Error {0}",err.getMessage()));
            return Mono.just(ResponseEntity.ok().body(new APIResponse(400,"fail","Failed to save external member details",Instant.now())));
        });
    }

    Mono<ResponseEntity<APIResponse>> saveDependent(String client, String title, String firstName, String surname, String idNumber, String gender, LocalDate dateOfBirth, String age, String mainMemberId, String policyNumber){
        return lifeInsuranceRepository.saveDependent(client,title,firstName,surname,idNumber,gender,dateOfBirth,age,mainMemberId,policyNumber).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed saving dependent details {0}",mainMemberId));
            return Mono.just(ResponseEntity.ok().body(new APIResponse(200,"success","Dependent details successfully saved",Instant.now())));
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save dependent details. Error {0}",err.getMessage()));
            return Mono.just(ResponseEntity.ok().body(new APIResponse(400,"fail","Failed to save dependent details",Instant.now())));
        });
    }

    Mono<ResponseEntity<APIResponse>> saveBeneficiary(String policyNumber,String client, String contactCell, String contactWorkTell,String contactHomeTell,String contactFax,String contactEmail,String sessionUserID, String prefType,String benLastName, String benFirstName, String benIdNumber, String benPercentage,String benDOB,String benRelation,String benType,String title){
        return lifeInsuranceRepository.saveBeneficiary(policyNumber,client,contactCell,contactWorkTell,contactHomeTell,contactFax,contactEmail,sessionUserID,prefType,benLastName,benFirstName,benIdNumber,benPercentage,benDOB,benRelation,benType,title).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed saving beneficiary details {0}",policyNumber));
            return Mono.just(ResponseEntity.ok().body(new APIResponse(200,"success","Beneficiary details successfully saved",Instant.now())));
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save beneficiary details. Error {0}",err.getMessage()));
            return Mono.just(ResponseEntity.ok().body(new APIResponse(400,"fail","Failed to save beneficiary details",Instant.now())));
        });
    }

    Mono<ResponseEntity<APIResponse>> saveBankDetails(String client,String policyNumber,String sessionUserID,String branchCode,String accNumber,String accType,String accName,String dedDay,LocalDate fromDate,String idNumber, String subNaedo){
        return lifeInsuranceRepository.saveBankDetails(client,policyNumber,sessionUserID,branchCode,accNumber,accType,accName,dedDay,fromDate,idNumber,subNaedo).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed saving bank details details {0}",policyNumber));
            return Mono.just(ResponseEntity.ok().body(new APIResponse(200,"success","Bank details successfully saved",Instant.now())));
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save bank details. Error {0}",err.getMessage()));
            return Mono.just(ResponseEntity.ok().body(new APIResponse(400,"fail","Failed to save bank details details",Instant.now())));
        });
    }

}
