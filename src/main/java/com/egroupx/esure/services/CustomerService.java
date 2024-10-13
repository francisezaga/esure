package com.egroupx.esure.services;

import com.egroupx.esure.model.*;
import com.egroupx.esure.model.responses.api.APIResponse;
import com.egroupx.esure.repository.AllRisksRepository;
import com.egroupx.esure.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;

@Service
public class CustomerService {

    @Value("${egroupx.services.fsp.endpointUrl}")
    private String fspEndpointUrl;

    @Value("${egroupx.services.fsp.apiKey:}")
    private String fspAPIKey;

    @Value("${egroupx.email.sendEmail}")
    private String sendEmail;

    private WebClient webClient;

    private final Logger LOG = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    private final AllRisksRepository allRisksRepository;

    private final EmailService emailService;

    public CustomerService(CustomerRepository customerRepository, AllRisksRepository allRisksRepository, EmailService emailService, AuthService authService) {
        this.customerRepository = customerRepository;
        this.allRisksRepository = allRisksRepository;
        this.emailService = emailService;
    }

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

    Mono<String> saveCustomerDetails(Long fspQouteRefId, Long fspPhrefId, String surname, String initials, String fullNames, String idNumber, String titleCd, String genderCd, LocalDate birthDate, String maritalStatusCd, String idType, String passportNumber){
        return customerRepository.saveCustomerDetails(fspQouteRefId,fspPhrefId,surname,initials,fullNames,idNumber,titleCd,genderCd,birthDate,maritalStatusCd,idType,passportNumber).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed customer details {0}",fspQouteRefId));
            return Mono.just("Completed saving customer details "+ fspQouteRefId);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save customer details {0}",err.getMessage()));
            return Mono.just("Failed to save customer details");
        });
    }

    public Mono<ResponseEntity<APIResponse>> getCustomerStep(String idNumber) {
        return customerRepository.findCustomerLastRecordStepByUserIdNumber(idNumber)
                .flatMap(custStep -> {
                    LOG.info(MessageFormat.format("Completed retrieving customer step details {0}", idNumber));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200, "success", custStep, Instant.now())));
                }).switchIfEmpty(Mono.defer(() -> {
                    LOG.error(MessageFormat.format("User {0} not found ", idNumber));
                    return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400, "Request failed", null, Instant.now())));
                }))
                .onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to retrieve step details. Error {0}", err.getMessage()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(400, "fail", null, Instant.now())));
                });
    }

    Mono<String> saveQuotationDetails(Long fspQouteRefId, String categoryId, String status){
            return customerRepository.saveQuotationDetails(fspQouteRefId, categoryId,status).flatMap(msg->{
                LOG.info(MessageFormat.format("Completed saving quotation details {0}",fspQouteRefId));
            return Mono.just("Completed saving quotation details "+ fspQouteRefId);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save quotation details {0}",err.getMessage()));
            return Mono.just("Failed to save quotation details");
        });
    }

    Mono<String> savePolicyHolderDetails(Long fspQouteRefId, Long fspPhrefId, String trust, String isPrivate, String stakeholderType) {
        return customerRepository.savePolicyHolderDetails(fspQouteRefId, fspPhrefId, trust, isPrivate, stakeholderType).then(Mono.just("Next")).flatMap(msg -> {
            LOG.info(MessageFormat.format("Completed saving policy holder details {0}", fspQouteRefId));
            return Mono.just("Completed saving policy holder details " + fspQouteRefId);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save policy holder details {0}", err.getMessage()));
            return Mono.just("Failed to save policy holder details");
        });
    }


    Mono<String> saveAddressDetails(Long fspQouteRefId,String typeCd,String line1,String code,String suburb,String residentialAreaType){
        return customerRepository.saveAddressDetails(fspQouteRefId,typeCd,line1,code,suburb,residentialAreaType).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed saving address details details {0}",fspQouteRefId));
            return Mono.just("Completed saving address details "+ fspQouteRefId);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save address details {0}",err.getMessage()));
            return Mono.just("Failed to save address details");
        });
    }

    Mono<String> saveTelAddressDetails(Long fspQouteRefId,String typeCd,String code,String number,String isCellphone,String isTelephone,String isBusiness,String isResidential){
        return customerRepository.saveTelAddressDetails(fspQouteRefId,typeCd,code,number,isCellphone,isTelephone,isBusiness,isResidential).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed saving tel address details {0}",fspQouteRefId));
            return Mono.just("Completed saving tel address details details "+ fspQouteRefId);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save tel address details {0}",err.getMessage()));
            return Mono.just("Failed to save tel address details");
        });
    }

    Mono<String> saveEmailAddressDetails(Long fspQouteRefId,String typeCd,String line1){
        return customerRepository.saveEmailAddressDetails(fspQouteRefId,typeCd,line1).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed saving email address details {0}",fspQouteRefId));
            return Mono.just("Completed saving email address details "+ fspQouteRefId);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save email address details {0}",err.getMessage()));
            return Mono.just("Failed to save email address details");
        });
    }

    Mono<String> savePolicyHolderShortTermDetails(Long fspQouteRefId, Long fspPhrefId, String heldInsuranceLast39Days, String periodCompCarInsurance, String periodCompNonMotorInsurance, String hasConsent){
        return customerRepository.savePolicyHolderShortTermDetails(fspQouteRefId,fspPhrefId,heldInsuranceLast39Days, periodCompCarInsurance,periodCompNonMotorInsurance,hasConsent).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed saving policy holder short term details {0}",fspQouteRefId));
            return Mono.just("Completed saving policy holder short term details "+ fspQouteRefId);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save policy holder short term details {0}",err.getMessage()));
            return Mono.just("Failed to save policy holder short term details");
        });
    }

    Mono<String> savePolicyHolderLicenseDetails(Long fspQouteRefId, Long fspPhrefId, LocalDate licenseDate, String licenseCategory,String licenseType, String vehicleRestriction){
        return customerRepository.savePolicyHolderLicenseDetails(fspQouteRefId,fspPhrefId,licenseDate,licenseCategory,licenseType,vehicleRestriction).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed saving policy holder license details {0}",fspQouteRefId));
            return Mono.just("Completed saving policy holder license details "+ fspQouteRefId);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save policy holder license details {0}",err.getMessage()));
            return Mono.just("Failed to save policy holder license details");
        });
    }

    Mono<String> savePolicyHolderIncomeDetails(Long fspQouteRefId,Long fspPhrefId,String occupationCategory){
        return customerRepository.savePolicyHolderIncomeDetails(fspQouteRefId,fspPhrefId,occupationCategory).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed saving policy holder income details {0}",fspQouteRefId));
            return Mono.just("Completed saving policy holder income details "+ fspQouteRefId);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save policy holder income details {0}",err.getMessage()));
            return Mono.just("Failed to save policy holder income details");
        });

    }

    Mono<String> saveAllRisksDetails(Long fspQouteRefId,String itemDescription,String sumInsured,String coverTypeId){
        return allRisksRepository.saveAllRisksDetails(fspQouteRefId,itemDescription,sumInsured,coverTypeId).flatMap(msg->{
            LOG.info(MessageFormat.format("Completed saving all risks details {0}",fspQouteRefId));
            return Mono.just("Completed saving all risks details "+ fspQouteRefId);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to save all risks details {0}",err.getMessage()));
            return Mono.just("Failed to save all risks details");
        });
    }

    Flux<Customer> getCustomerQuotationByUserId(String idNumber){
        return customerRepository.getCustomerRecordsByUserId(idNumber).flatMap(records ->{
           return Flux.just(records);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to retrieve customer records by id. Error {0}",err.getMessage()));
            return Flux.empty();
        });
    }

    Mono<Customer> getCustomerByQuoteRef(Long fspQuoteRef) {
        return customerRepository.getCustomerByQuoteRefId(fspQuoteRef).flatMap(customer -> {
            return Mono.just(customer);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to retrieve customer by ref id. Error {0}", err.getMessage()));
            return Mono.empty();
        });
    }

    Mono<Quotation> getQuotationByQouteRefId(Long qouteRefId) {
        return customerRepository.getQuotationByQuoteRefId(qouteRefId).flatMap(quote -> {
            LOG.info(MessageFormat.format("Successfully retrieved quotation {0}",qouteRefId));
            return Mono.just(quote);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to retrieve quote by id {0}. Error {1}",qouteRefId, err.getMessage()));
            return Mono.empty();
        });
    }

    Flux<PolicyHolder> getPolicyHolderByQouteRefId(Long qouteRefId) {
        return customerRepository.getPolicyHolderByQuoteRefId(qouteRefId).flatMap(ph -> {
            LOG.info(MessageFormat.format("Successfully retrieved policy holder {0}",qouteRefId));
            return Flux.just(ph);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to retrieve policy holder by id {0}. Error {1}",qouteRefId, err.getMessage()));
            return Flux.empty();
        });
    }

    Mono<PHShortTerm> getPHShortTermByQouteRefId(Long qouteRefId) {
        return customerRepository.getPHShortTermByQuoteRefId(qouteRefId).flatMap(phShortTerm -> {
            LOG.info(MessageFormat.format("Successfully retrieved policy holder short term {0}",qouteRefId));
            return Mono.just(phShortTerm);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to retrieve policy holder short term by id {0}. Error {1}",qouteRefId, err.getMessage()));
            return Mono.empty();
        });
    }

    Flux<PHLicenseDetail> getPHLicenseDetailQuoteRefId(Long qouteRefId) {
        return customerRepository.getPHLicenseDetailByQuoteRefId(qouteRefId).flatMap(phLicenseDetail -> {
            LOG.info(MessageFormat.format("Successfully retrieved policy holder short term license detail {0}",qouteRefId));
            return Mono.just(phLicenseDetail);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to retrieve policy holder short term license detail by id {0}. Error {1}",qouteRefId, err.getMessage()));
            return Mono.empty();
        });
    }

    Mono<PHIncomeDetails> getPHIncomeDetails(Long qouteRefId) {
        return customerRepository.getPHIncomeDetailsByQuoteRefId(qouteRefId).flatMap(phIncomeDetails -> {
            LOG.info(MessageFormat.format("Successfully retrieved policy holder income detail {0}",qouteRefId));
            return Mono.just(phIncomeDetails);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to retrieve policy holder income detail by id {0}. Error {1}",qouteRefId, err.getMessage()));
            return Mono.empty();
        });
    }

    Flux<Address> getAddressDetails(Long qouteRefId) {
        return customerRepository.getAddressByQuoteRefId(qouteRefId).flatMap(address -> {
            LOG.info(MessageFormat.format("Successfully retrieved address detail {0}",qouteRefId));
            return Mono.just(address);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to retrieve address detail by id {0}. Error {1}",qouteRefId, err.getMessage()));
            return Mono.empty();
        });
    }

    Flux<TelAddress> getTelAddressDetails(Long qouteRefId) {
        return customerRepository.getTelAddressByQuoteRefId(qouteRefId).flatMap(telAddress -> {
            LOG.info(MessageFormat.format("Successfully retrieved tel address detail {0}",qouteRefId));
            return Mono.just(telAddress);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to retrieve tel address detail by id {0}. Error {1}",qouteRefId, err.getMessage()));
            return Mono.empty();
        });
    }

    Flux<EmailAddress> getEmailAddressDetails(Long qouteRefId) {
        return customerRepository.getEmailAddressByQuoteRefId(qouteRefId).flatMap(emailAddress -> {
            LOG.info(MessageFormat.format("Successfully retrieved email address detail {0}",qouteRefId));
            return Mono.just(emailAddress);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to retrieve email address detail by id {0}. Error {1}",qouteRefId, err.getMessage()));
            return Mono.empty();
        });
    }

    Flux<AllRisks> getAllRisksDetails(Long qouteRefId) {
        return allRisksRepository.getAllRisksByQuoteRefId(qouteRefId).flatMap(allrisks -> {
            LOG.info(MessageFormat.format("Successfully retrieved all risks detail {0}",qouteRefId));
            return Mono.just(allrisks);
        }).onErrorResume(err -> {
            LOG.error(MessageFormat.format("Failed to retrieve all risks detail by id {0}. Error {1}",qouteRefId, err.getMessage()));
            return Mono.empty();
        });
    }


    Mono<String> sendEmailQuotationNotification(Long fspQuoteId) {
        return customerRepository.getCustomerEmailDetailsByQuoteRef(fspQuoteId)
                .flatMap(customer -> {
                    return emailService.sendFSPQuotationNotificationEmail(customer,"New Quote Request:"+" "+fspQuoteId).flatMap(Mono::just);
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to send email quote ref {0}. Error {1}",fspQuoteId, err.getMessage()));
                    return Mono.just("Failed to send email");
                });
    }

    Mono<String> sendEmailFSPCustomerWelcomeNotification(Long fspQuoteId) {
        return customerRepository.getCustomerEmailDetailsByQuoteRef(fspQuoteId)
                .flatMap(customer -> {
                    return emailService.sendFSPInsuranceWelcomeEmail(customer,"Welcome to eSure Insurance – We're Excited to Have You!").flatMap(Mono::just);
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to send email quote ref {0}. Error {1}",fspQuoteId, err.getMessage()));
                    return Mono.just("Failed to send email");
                });
    }

}
