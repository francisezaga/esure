package com.egroupx.esure.controller;

import com.egroupx.esure.dto.life.MemberDTO;
import com.egroupx.esure.model.medical_aid.MedicalAidMemberDetails;
import com.egroupx.esure.model.responses.api.APIResponse;
import com.egroupx.esure.services.MedicalAidService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/esure/medical")
public class MedicalAidController {

    private final MedicalAidService medicalAidService;

    public MedicalAidController(MedicalAidService medicalAidService) {
        this.medicalAidService = medicalAidService;
    }

    @PostMapping(value = {"/saveMedicalAidMemberDetails"})
    public Mono<ResponseEntity<APIResponse>> saveMemberPersonalDetails(@RequestBody MedicalAidMemberDetails memberDetails)  {
        return medicalAidService.saveMedicalAidMemberPersonalDetails(memberDetails);
    }
}
