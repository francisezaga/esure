package com.egroupx.esure.services;

import com.egroupx.esure.model.medical_aid.MedicalAidMemberDetails;
import com.egroupx.esure.model.responses.api.APIResponse;
import com.egroupx.esure.repository.MedicalAidRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Instant;

@Service
public class MedicalAidService {


    @Value("${egroupx.services.medicalAid.pfisterEmailAddress}")
    private String pfisterEmailAddress;

    @Value("${egroupx.services.medicalAid.esureEmailAddress}")
    private String esureEmailAddress;

    private final MedicalAidRepository medicalAidRepository;

    private final EmailService emailService;

    private final Logger LOG = LoggerFactory.getLogger(MedicalAidService.class);

    public MedicalAidService(MedicalAidRepository medicalAidRepository, EmailService emailService) {
        this.medicalAidRepository = medicalAidRepository;
        this.emailService = emailService;
    }


    public Mono<ResponseEntity<APIResponse>> saveMedicalAidMemberPersonalDetails(MedicalAidMemberDetails memberDetails) {

        return medicalAidRepository.findMedicalAidRecordByEmailAddress(memberDetails.getEmail())
                .flatMap(member -> {
                    LOG.info(MessageFormat.format("Medical aid member already exist. {0}", memberDetails.getPhoneNumber()));
                    return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400, "fail", "Medical aid member details already exist", Instant.now())));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    LOG.error(MessageFormat.format("Medical aid member does not exist {0}. Saving member ", memberDetails.getPhoneNumber()));
                    return medicalAidRepository.saveMedicalAidDetails(memberDetails.getAdultsCount(), memberDetails.getChildrenCount(), memberDetails.getFirstName(), memberDetails.getLastName(), memberDetails.getEmail(), memberDetails.getPhoneNumber(), memberDetails.getDateOfBirth(), memberDetails.isHasMedicalAid(), memberDetails.getNameOfMedicalAidProvider(), memberDetails.isGrossIncomeMoreThan14K(), memberDetails.getBudgetedAmount(), memberDetails.getMedicalPriority(), memberDetails.isNetIncomeMoreThan14k(), memberDetails.isMemberOrDependentHasChronicMedRequirements()).then(Mono.just("next"))
                            .flatMap(msg -> {
                                LOG.info(MessageFormat.format("Completed saving medical aid member personal details {0}", memberDetails.getPhoneNumber()));
                                return sendEmailLifeCoverNotification(memberDetails.getEmail()).flatMap(res -> {
                                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200, "success", "Medical aid details saved", Instant.now())));
                                });
                            }).onErrorResume(err -> {
                                LOG.error(MessageFormat.format("Failed to save medical aid details. Error {0}", err.getMessage()));
                                return Mono.just(ResponseEntity.ok().body(new APIResponse(400, "fail", "Failed to save medical aid details", Instant.now())));
                            });
                }))
                .onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save medical aid member details. Error {0}", err.getMessage()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(400, "fail", "Failed to save medical aid details", Instant.now())));
                });
    }

    Mono<String> sendEmailLifeCoverNotification(String email) {
        return medicalAidRepository.findMedicalAidRecordByEmailAddress(email)
                .flatMap(member -> {
                    return emailService.sendQuotationEmailForMedicalAid(member, "New Medical Aid Lead from eSure Cover", "Pfister & Associates Team",pfisterEmailAddress)
                            .flatMap(Mono::just).then(Mono.just("Send email to esure call center"))
                            .flatMap(esure -> emailService.sendQuotationEmailForMedicalAid(member, "New Medical Aid Lead from eSure Cover", "eSure Cover Medical Aid Support",esureEmailAddress))
                            .then(Mono.just("Send welcome email to customer")).flatMap(welcome -> emailService.sendWelcomeEmailForMedicalAid(member, "Welcome to eSure Cover Medical Aid! - We're Excited to Have You").flatMap(Mono::just));
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to send email for medical aid ref {0}. Error {1}", email, err.getMessage()));
                    return Mono.just("Failed to send email");
                });
    }
}
