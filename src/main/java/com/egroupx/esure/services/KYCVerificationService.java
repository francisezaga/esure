package com.egroupx.esure.services;

import com.egroupx.esure.dto.kyc.NameScanDTO;
import com.egroupx.esure.dto.kyc.PersonalDetailsDTO;
import com.egroupx.esure.dto.kyc.RSAIDcheckDTO;
import com.egroupx.esure.model.Customer;
import com.egroupx.esure.model.responses.api.APIResponse;
import com.egroupx.esure.model.responses.kyc.CitizenResponse;
import com.egroupx.esure.model.responses.kyc.NameScanResponse;
import com.egroupx.esure.repository.CustomerRepository;
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

@Service
public class KYCVerificationService {

    @Value("${egroupx.services.namescan.url}")
    private String nameSpaceScanUrl;

    @Value("${egroupx.services.namescan.apiKey}")
    private String nameSpaceKey;

    @Value("${egroupx.services.securecitizen.url}")
    private String citizenUrl;

    private final TokenService tokenService;

    private final CustomerRepository customerRepository;

    private final LifeInsuranceRepository lifeInsuranceRepository;

    private final Logger LOG = LoggerFactory.getLogger(TokenService.class);

    private final EmailService emailService;

    private WebClient webClientPost;

    public KYCVerificationService(TokenService tokenService, CustomerRepository customerRepository, LifeInsuranceRepository lifeInsuranceRepository, EmailService emailService) {
        this.tokenService = tokenService;
        this.customerRepository = customerRepository;
        this.lifeInsuranceRepository = lifeInsuranceRepository;
        this.emailService = emailService;
    }

    private void setConfigs(String endpointBaseUrl){

        this.webClientPost = WebClient.builder()
                .baseUrl(endpointBaseUrl)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE,
                        "Content-Type", MediaType.APPLICATION_JSON_VALUE
                ).build();
    }


    public Mono<APIResponse> nameScanVerification(NameScanDTO nameScanReq) {
        setConfigs(nameSpaceScanUrl);
        return webClientPost.post()
                .uri("/person-scans/emerald")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, "*/*")
                .header("api-key", nameSpaceKey)
                .bodyValue(nameScanReq)
                .retrieve()
                .toEntity(NameScanResponse.class).map(responseEntity -> {
                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                        NameScanResponse nameSpaceRes = responseEntity.getBody();
                        if (nameSpaceRes != null && nameSpaceRes.getNumberOfMatches() == 0) {
                            LOG.info(MessageFormat.format("User Verified. Number of matchers for user {0} {1} is 0", nameScanReq.getFirstName(), nameScanReq.getLastName()));
                            return new APIResponse(200, "success", "Verified no match found. Number of matchers " + nameSpaceRes.getNumberOfMatches(), Instant.now());
                        } else {
                            LOG.error(MessageFormat.format("User {0} {1} not verified. Number of matchers for user {2}", nameScanReq.getFirstName(), nameScanReq.getLastName(), nameSpaceRes.getNumberOfMatches()));
                            return new APIResponse(200, "fail", "User verification failed but user allowed to proceed. Scan found multiple matches " + nameSpaceRes.getNumberOfMatches(), Instant.now());
                        }
                    } else {
                        LOG.error(MessageFormat.format("User {0} {1} could not be verified {2}", nameScanReq.getFirstName(), nameScanReq.getLastName(), responseEntity.getStatusCode()));
                        return new APIResponse(500, "fail", "User " + nameScanReq.getFirstName() + " " + nameScanReq.getLastName() + " not verified " + responseEntity.getStatusCode(), Instant.now());
                    }
                }).onErrorResume(error -> {
                    LOG.info(MessageFormat.format("Unexpected error. Personal details could not be verified on namespace scan {0}", error.getMessage()));
                    return Mono.just(new APIResponse(500, "fail", "Unexpected error. Personal details could not be verified on namespace scan ", Instant.now()));
                });
    }

    public Mono<APIResponse> rSAIDCheckVerification(RSAIDcheckDTO rsaCitiReq) {
        setConfigs(citizenUrl);

        return tokenService.getCitizenAPIToken().flatMap(
                bearerToken ->
                        webClientPost.post()
                                .uri("/RSAIDVerification")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .header(HttpHeaders.ACCEPT, "*/*")
                                .headers((headers -> headers.add("authorization", "Bearer " + bearerToken)))
                                .bodyValue(rsaCitiReq)
                                .retrieve()
                                .toEntity(CitizenResponse.class).map(responseEntity -> {
                                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                                        CitizenResponse citizenRes = responseEntity.getBody();
                                        if (citizenRes != null && citizenRes.getStatus() == 200) {
                                    /*if(strRes.toLowerCase().contains("status=failure")) {
                                        LOG.info(MessageFormat.format("Request failed. User ID number seems to be valid . API response status {0}, details status is {1}", citizenRes.getStatus(),"Status=Failure"));
                                        return new APIResponse(400, "Request failed", "User ID details could not be verified. API response status "+citizenRes.getStatus()+ "check status is Status=Failure" , Instant.now());
                                    }else{*/
                                            LOG.info(MessageFormat.format("User ID Verified. Response status {0} details status is ", citizenRes.getStatus(),citizenRes.getResponse().getStatus()));
                                            return new APIResponse(200, "success", "User ID Verified. Response status "  + citizenRes.getStatus() + "details status detail is "+citizenRes.getResponse().getStatus(), Instant.now());
                                            //}
                                        } else {
                                            LOG.error(MessageFormat.format("User ID verification failed. Response status {0}", citizenRes.getStatus()));
                                            return new APIResponse(400, "fail", "User ID verification failed. Response status " + citizenRes.getStatus(), Instant.now());
                                        }
                                    } else {
                                        LOG.error(MessageFormat.format("User ID verification failed. Response status {0}", responseEntity.getStatusCode().value()));
                                        return new APIResponse(500, "fail", "User ID verification failed. Response status " + responseEntity.getStatusCode().value(), Instant.now());
                                    }
                                }).onErrorResume(error -> {
                                    LOG.error(MessageFormat.format("Unexpected error. Personal details could not be verified on ID {0}", error.getMessage()));
                                    return Mono.just(new APIResponse(500, "fail", "Unexpected error. Personal details could not be verified on ID ", Instant.now()));
                                })).switchIfEmpty(Mono.defer(() -> {
            LOG.error("Request failed on verifying ID. Failed to retrieve citizen API token");
            return Mono.just(new APIResponse(500, "fail", "Request failed on verifying ID. Failed to retrieve citizen API token", Instant.now()));
        })).onErrorResume(error -> {
            LOG.info(MessageFormat.format("Request failed on verifying ID. Failed to retrieve citizen API token {0} ", error.getMessage()));
            return Mono.just(new APIResponse(500, "fail", "Request failed on verifying ID. Failed to retrieve citizen API token ", Instant.now()));
        });
    }

    public Mono<ResponseEntity<APIResponse>> verifyPersonalDetailsForInsurance(String idNumber) {


        return customerRepository.findCustomerLastRecordsByUserIdNumber(idNumber).flatMap(personalDetails -> {
                    PersonalDetailsDTO personalDetailsDTO = getPersonalDetailsDTO(personalDetails);

                    NameScanDTO nameScanReq = new NameScanDTO();
                    nameScanReq.setFirstName(personalDetailsDTO.getFirstName());
                    nameScanReq.setLastName(personalDetails.getSurname());

                    LOG.info(MessageFormat.format("Verifying name scan personal details for user customer {0}", idNumber));
                    return nameScanVerification(nameScanReq).flatMap(nameScanRes -> {
                        int nameScanResCode = nameScanRes.getStatus();

                        return switch (nameScanResCode) {
                            case 200 -> {
                                RSAIDcheckDTO rsaiDcheckRequest = getRsaiDcheckRequest(personalDetailsDTO);
                                if (personalDetailsDTO.getTypeOfId().equalsIgnoreCase("id")) {
                                    yield rSAIDCheckVerification(rsaiDcheckRequest).flatMap(
                                            idCheckRes -> {
                                                int rsaIDVerResCode = idCheckRes.getStatus();

                                                boolean isNameScanPass = true;
                                                String nameScanFailReason ="";
                                                boolean isKycIdVerified=true;
                                                String kycIdVerFailReason="";

                                                if(nameScanRes.getMessage().equalsIgnoreCase("fail")){
                                                    isNameScanPass = false;
                                                    nameScanFailReason= nameScanRes.getData().toString();
                                                }

                                                if(idCheckRes.getMessage().equalsIgnoreCase("fail")){
                                                    isKycIdVerified = false;
                                                    kycIdVerFailReason = idCheckRes.getData().toString();
                                                }

                                                return switch (rsaIDVerResCode) {
                                                    case 200 -> updateCustomerKYCDetails(idNumber,isNameScanPass,nameScanFailReason,isKycIdVerified,kycIdVerFailReason,personalDetails.getId());
                                                    case 400 -> Mono.just(ResponseEntity.badRequest().body(idCheckRes));
                                                    //default -> Mono.just(ResponseEntity.internalServerError().body(idCheckRes));
                                                    default->updateCustomerKYCDetails(idNumber,isNameScanPass,nameScanFailReason,isKycIdVerified,kycIdVerFailReason,personalDetails.getId());

                                                };
                                            }

                                    );
                                } else {
                                    boolean isNameScanPass = true;
                                    String nameScanFailReason ="";
                                    if(nameScanRes.getMessage().equalsIgnoreCase("fail")){
                                        isNameScanPass = false;
                                        nameScanFailReason= nameScanRes.getData().toString();
                                    }
                                    boolean isKycIdVerified=false;
                                    String kycIdVerFailReason="";
                                    yield updateCustomerKYCDetails(idNumber,isNameScanPass,nameScanFailReason,isKycIdVerified,kycIdVerFailReason,personalDetails.getId());
                                }
                            }
                            case 400 -> Mono.just(ResponseEntity.badRequest().body(nameScanRes));
                            default -> Mono.just(ResponseEntity.internalServerError().body(nameScanRes));
                        };
                    });

                }).onErrorResume(error -> {
                    LOG.error(MessageFormat.format("Error checking user {0} {1}", idNumber, error.getMessage()));
                    return Mono.just(ResponseEntity.internalServerError().body(new APIResponse(500, "Request failed", "Error checking user. Please try again", Instant.now())));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    LOG.error(MessageFormat.format("User {0} not found ", idNumber));
                    return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400, "Request failed", "User not found. Please try again", Instant.now())));
                }));

    }

    private static PersonalDetailsDTO getPersonalDetailsDTO(Customer personalDetails) {
        String firstName = (personalDetails.getFullNames()!=null && !personalDetails.getFullNames().isEmpty())? personalDetails.getFullNames().split(" ")[0]:"";
        PersonalDetailsDTO personalDetailsDTO = new PersonalDetailsDTO();
        personalDetailsDTO.setFirstName(firstName);
        personalDetailsDTO.setLastName(personalDetails.getSurname());
        personalDetailsDTO.setIdNumber(personalDetails.getIdNumber());
        personalDetailsDTO.setTypeOfId(personalDetails.getIdType().equalsIgnoreCase("1")?"id":"passport");
        return personalDetailsDTO;
    }

    public Mono<ResponseEntity<APIResponse>> verifyPersonalDetailsForLifeCover(String idNumber) {

        LOG.info(idNumber);
        return lifeInsuranceRepository.findMemberLastRecordByIdNumber(idNumber)
                .flatMap(personalDetails -> {
                    String idType =(personalDetails.getTypeOfId()!=null && (personalDetails.getTypeOfId().equalsIgnoreCase("id") || personalDetails.getTypeOfId().equalsIgnoreCase("1")))?"id":"passport";
                    PersonalDetailsDTO personalDetailsDTO = new PersonalDetailsDTO();
                    personalDetailsDTO.setIdNumber(personalDetails.getIdNumber());
                    personalDetailsDTO.setFirstName(personalDetails.getFirstName());
                    personalDetailsDTO.setLastName(personalDetails.getSurname());
                    personalDetailsDTO.setTypeOfId(personalDetails.getTypeOfId()==null?"id":idType);

                    LOG.info(String.valueOf(personalDetails.getId()));

                    NameScanDTO nameScanReq = new NameScanDTO();
                    nameScanReq.setFirstName(personalDetailsDTO.getFirstName());
                    nameScanReq.setLastName(personalDetails.getSurname());

                    LOG.info(MessageFormat.format("Verifying name scan personal details for user member {0}", idNumber));
                    return nameScanVerification(nameScanReq).flatMap(nameScanRes -> {
                        int nameScanResCode = nameScanRes.getStatus();

                        return switch (nameScanResCode) {
                            case 200 -> {
                                RSAIDcheckDTO rsaiDcheckRequest = getRsaiDcheckRequest(personalDetailsDTO);
                                if (personalDetailsDTO.getTypeOfId().equalsIgnoreCase("id")) {
                                    yield rSAIDCheckVerification(rsaiDcheckRequest).flatMap(
                                            idCheckRes -> {
                                                int rsaIDVerResCode = idCheckRes.getStatus();

                                                boolean isNameScanPass = true;
                                                String nameScanFailReason ="";
                                                boolean isKycIdVerified=true;
                                                String kycIdVerFailReason="";

                                                if(nameScanRes.getMessage().equalsIgnoreCase("fail")){
                                                    isNameScanPass = false;
                                                    nameScanFailReason= nameScanRes.getData().toString();
                                                }

                                                if(idCheckRes.getMessage().equalsIgnoreCase("fail")){
                                                    isKycIdVerified = false;
                                                    kycIdVerFailReason = idCheckRes.getData().toString();
                                                }

                                                return switch (rsaIDVerResCode) {
                                                    case 200 -> updateMemberKYCDetails(idNumber,isNameScanPass,nameScanFailReason,isKycIdVerified,kycIdVerFailReason,personalDetails.getId());
                                                    case 400 -> Mono.just(ResponseEntity.badRequest().body(idCheckRes));
                                                    //default -> Mono.just(ResponseEntity.internalServerError().body(idCheckRes));
                                                    default-> updateMemberKYCDetails(idNumber,isNameScanPass,nameScanFailReason,isKycIdVerified,kycIdVerFailReason,personalDetails.getId());

                                                };
                                            }

                                    );
                                } else {
                                    boolean isNameScanPass = true;
                                    String nameScanFailReason ="";
                                    if(nameScanRes.getMessage().equalsIgnoreCase("fail")){
                                        isNameScanPass = false;
                                        nameScanFailReason= nameScanRes.getData().toString();
                                    }
                                    boolean isKycIdVerified=false;
                                    String kycIdVerFailReason="";
                                    yield updateMemberKYCDetails(idNumber,isNameScanPass,nameScanFailReason,isKycIdVerified,kycIdVerFailReason,personalDetails.getId());
                                }
                            }
                            case 400 -> Mono.just(ResponseEntity.badRequest().body(nameScanRes));
                            default -> Mono.just(ResponseEntity.internalServerError().body(nameScanRes));
                        };
                    });

                })
                .switchIfEmpty(Mono.defer(() -> {
                    LOG.error(MessageFormat.format("User {0} not found ", idNumber));
                    return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400, "Request failed", "User not found. Please try again", Instant.now())));
                }))
                .onErrorResume(error -> {
                    LOG.error(MessageFormat.format("Error checking user {0} {1}", idNumber, error.getMessage()));
                    return Mono.just(ResponseEntity.internalServerError().body(new APIResponse(500, "Request failed", "Error checking user. Please try again", Instant.now())));
                });

    }

    private static RSAIDcheckDTO getRsaiDcheckRequest(PersonalDetailsDTO personalDetailsDTO) {
        if(personalDetailsDTO!=null) {
            RSAIDcheckDTO rsaiDcheckReq = new RSAIDcheckDTO();
            rsaiDcheckReq.setCref("cref" + personalDetailsDTO.getIdNumber());
            rsaiDcheckReq.setFirstNames(personalDetailsDTO.getFirstName());
            rsaiDcheckReq.setLastName(personalDetailsDTO.getLastName());
            rsaiDcheckReq.setIdNumber(personalDetailsDTO.getIdNumber());
            rsaiDcheckReq.setSubsidiary("eZaga");
            rsaiDcheckReq.setRequestReason("KYC Check");
            rsaiDcheckReq.setSAFPSRequired(true);
            rsaiDcheckReq.setIdentityCache(true);
            rsaiDcheckReq.setLivenessRequired(false);
            rsaiDcheckReq.setHANISImageRequired(true);
            rsaiDcheckReq.setIdentityCache(true);
            rsaiDcheckReq.setConsentReceived(true);
            return rsaiDcheckReq;
        }
        else{
            return new RSAIDcheckDTO();
        }
    }

    Mono<ResponseEntity<APIResponse>> updateCustomerKYCDetails(String idNumber,boolean nameSpaceScanPass,String nameSpaceScanFailReason, boolean idVerificationPass,String idVerificationFailReason,Long id){
        return customerRepository.updateCustomerKycDetails(nameSpaceScanPass,nameSpaceScanFailReason,idVerificationPass,idVerificationFailReason,id).then(Mono.just("next")).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed updating customer kyc verification details {0}",id));
            return updateCustomerStepDetails("APPLICATION_COMPLETED",id)
                    .then(Mono.just("done")).flatMap(done->{
                        return Mono.just(ResponseEntity.ok().body(new APIResponse(200,"success","Completed updating customer kyc verification details "+ id,Instant.now())));
                    });
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to update customer kyc verification details {0}",err.getMessage()));
           return updateCustomerStepDetails("APPLICATION_COMPLETED",id)
                   .then(Mono.just("done")).flatMap(done->{
                       return Mono.just(ResponseEntity.ok().body(new APIResponse(200,"success","Completed updating customer kyc verification details "+ id,Instant.now())));
                   });
        });
    }

    Mono<ResponseEntity<APIResponse>> updateMemberKYCDetails(String idNumber,boolean nameSpaceScanPass,String nameSpaceScanFailReason, boolean idVerificationPass,String idVerificationFailReason,Long id){
        return lifeInsuranceRepository.updateMemberKycDetails(nameSpaceScanPass,nameSpaceScanFailReason,idVerificationPass,idVerificationFailReason,id).then(Mono.just("next")).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed updating member kyc verification details {0}",id));
            return sendEmailLifeCoverNotification(idNumber)
                    .then(Mono.just("next")).flatMap(nextStep->{
                        return Mono.just("next");
                    }).then(Mono.just("next")).flatMap(next->{
                        return updateMemberStepDetails("APPLICATION_COMPLETED",id)
                                .then(Mono.just("done")).flatMap(done->{
                                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200,"success","Completed updating member kyc verification details "+ id,Instant.now())));
                                });

                    });
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to update member kyc verification details {0}",err.getMessage()));
            return sendEmailLifeCoverNotification(idNumber)
                    .then(Mono.just("next")).flatMap(nextStep->{
                        return Mono.just("next");
                    }).then(Mono.just("next")).flatMap(next->{
                        return updateMemberStepDetails("APPLICATION_COMPLETED",id)
                                .then(Mono.just("done")).flatMap(done->{
                                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200,"success","Completed updating member kyc verification details "+ id,Instant.now())));
                                });

                    });
            //return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400,"fail","Failed to update member verification details",Instant.now())));
        });
    }

    Mono<String> sendEmailLifeCoverNotification(String idNumber) {
        return lifeInsuranceRepository.findMemberLastRecordByIdNumber(idNumber)
                .flatMap(member -> {
                    return emailService.sendEmailForLifeCover(member,"New Esure Life Cover").flatMap(Mono::just);
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to send email life cover ref {0}. Error {1}",idNumber, err.getMessage()));
                    return Mono.just("Failed to send email");
                });
    }

    public Mono<String> updateCustomerStepDetails(String step,Long id){
        return customerRepository.updateCustomerStepDetails(step,id).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed updating customer step details {0}",id));
            return Mono.just("Completed updating customer step details "+ id);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to update customer step details {0}",err.getMessage()));
            return Mono.just("Failed to update customer step  details");
        });
    }

    public Mono<String> updateMemberStepDetails(String step, Long id) {
        return lifeInsuranceRepository.updateMemberStepDetails(step, id).flatMap(msg -> {
            LOG.info(MessageFormat.format("Completed updating member step details {0}", id));
            return Mono.just("Completed updating member step details " + id);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to update member step details {0}", err.getMessage()));
            return Mono.just("Failed to update member step  details");
        });
    }
}
