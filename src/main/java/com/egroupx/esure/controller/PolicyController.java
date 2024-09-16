package com.egroupx.esure.controller;

import com.egroupx.esure.dto.fsp_policy.Policy;
import com.egroupx.esure.model.responses.api.APIResponse;
import com.egroupx.esure.services.PolicyService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/esure/policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping(value = {"/acceptPolicy/{policyId}"},consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<APIResponse>> acceptPolicy(@PathVariable Long policyId, @RequestBody Policy policy)  {
        return policyService.acceptPolicy(policyId,policy);
    }

    /*@PostMapping(value = {"/getPolicyById/{policyId}"},consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<APIResponse>> getPolicyById(@PathVariable Long policyId)  {
        return policyService.getPolicyById(policyId);
    }*/

    @GetMapping(value = {"/getPoliciesByUserIdNumber/{idNumber}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<APIResponse>> getPolicyByUserId(@PathVariable String idNumber)  {
        return policyService.getPolicyByUserId(idNumber);
    }
}
