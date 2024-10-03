package com.egroupx.esure.services;


import com.egroupx.esure.dto.life.MemberDTO;
import com.egroupx.esure.model.medical_aid.MedicalAidMemberDetails;
import com.egroupx.esure.model.responses.api.APIResponse;
import com.egroupx.esure.repository.MedicalAidRepository;
import com.egroupx.esure.util.AppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Instant;

@Service
public class MedicalAidService {

    private final MedicalAidRepository medicalAidRepository;

    private final EmailService emailService;

    private final Logger LOG = LoggerFactory.getLogger(MedicalAidService.class);

    public MedicalAidService(MedicalAidRepository medicalAidRepository, EmailService emailService) {
        this.medicalAidRepository = medicalAidRepository;
        this.emailService = emailService;
    }


    public Mono<ResponseEntity<APIResponse>> saveMedicalAidMemberPersonalDetails(MedicalAidMemberDetails memberDetails) {

        return medicalAidRepository.findMedicalAidRecordByPhoneNumber(memberDetails.getPhoneNumber())
                .flatMap(member -> {
                    LOG.info(MessageFormat.format("Medical aid member already exist. {0}", memberDetails.getPhoneNumber()));
                    return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400,"fail","Medical aid member details already exist",Instant.now())));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    LOG.error(MessageFormat.format("Medical aid member does not exist {0}. Saving member ", memberDetails.getPhoneNumber()));
                    return medicalAidRepository.saveMedicalAidDetails(memberDetails.getAdultsCount(),memberDetails.getChildrenCount(),memberDetails.getFullName(),memberDetails.getEmail(),memberDetails.getPhoneNumber(), memberDetails.isHasMedicalAid(),memberDetails.getIncomeCategory(),memberDetails.getHospitalChoice(),memberDetails.getHospitalRates(),memberDetails.getDayToDayCoverLevel(),memberDetails.getDoctorChoice(),memberDetails.isHasChronicMedicationRequirements(),memberDetails.getHospitalExclusions()).then(Mono.just("next"))
                            .flatMap(msg -> {
                                LOG.info(MessageFormat.format("Completed saving medical aid member personal details {0}", memberDetails.getPhoneNumber()));
                                return sendEmailLifeCoverNotification(memberDetails.getPhoneNumber()).flatMap(res-> {
                                    return Mono.just(ResponseEntity.ok().body(new APIResponse(200,"success","Medical aid details saved",Instant.now())));
                                });
                            }).onErrorResume(err -> {
                                LOG.error(MessageFormat.format("Failed to save medical aid details. Error {0}", err.getMessage()));
                                return Mono.just(ResponseEntity.ok().body(new APIResponse(400,"fail","Failed to save medical aid details",Instant.now())));
                            });
                }))
                .onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save medical aid member details. Error {0}", err.getMessage()));
                    return Mono.just(ResponseEntity.ok().body(new APIResponse(400,"fail","Failed to save medical aid details",Instant.now())));
                });
    }

    Mono<String> sendEmailLifeCoverNotification(String phoneNumber) {
        return  medicalAidRepository.findMedicalAidRecordByPhoneNumber(phoneNumber)
                .flatMap(member -> {
                    return emailService.sendEmailForMedicalAid(member, "New eSure Request To Open A Medical Aid").flatMap(msg->{
                       return emailService.sendWelcomeEmailForMedicalAid(member,"Welcome To eSure Medical Aid").flatMap(Mono::just);
                    });

                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to send email for medical aid ref {0}. Error {1}", phoneNumber, err.getMessage()));
                    return Mono.just("Failed to send email");
                });
    }
}
