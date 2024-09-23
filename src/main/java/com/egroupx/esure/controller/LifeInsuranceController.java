package com.egroupx.esure.controller;

import com.egroupx.esure.dto.v360.ProductDTO;
import com.egroupx.esure.model.life.*;
import com.egroupx.esure.model.responses.api.APIResponse;
import com.egroupx.esure.services.LifeInsuranceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/esure/life")
public class LifeInsuranceController {

    private final LifeInsuranceService lifeInsuranceService;

    public LifeInsuranceController(LifeInsuranceService lifeInsuranceService) {
        this.lifeInsuranceService = lifeInsuranceService;
    }

    @PostMapping(value = {"/createMember"})
    public Mono<ResponseEntity<APIResponse>> createMember(@RequestBody Member member)  {
        return lifeInsuranceService.createMember(member);
    }

    @PostMapping(value = {"/createBeneficiary"})
    public Mono<ResponseEntity<APIResponse>> createBeneficiary(@RequestBody Beneficiary beneficiary)  {
        return lifeInsuranceService.createBeneficiary(beneficiary);
    }

    @PostMapping(value = {"/createDependent"})
    public Mono<ResponseEntity<APIResponse>> createDependent(@RequestBody Dependent dependent)  {
        return lifeInsuranceService.createDependent(dependent);
    }

    @PostMapping(value = {"/createExtendedMember"})
    public Mono<ResponseEntity<APIResponse>> createExMember(@RequestBody ExtendedMember extendedMember)  {
        return lifeInsuranceService.createExtendedMember(extendedMember);
    }
    @PostMapping(value = {"/createSpouse"})
    public Mono<ResponseEntity<APIResponse>> createSpouse(@RequestBody Spouse spouse)  {
        return lifeInsuranceService.createSpouse(spouse);
    }
    @PostMapping(value = {"/addBankDetails"})
    public Mono<ResponseEntity<APIResponse>> addBankDetails(@RequestBody BankDetails bankDetails)  {
        return lifeInsuranceService.addBankDetails(bankDetails);
    }

    @PostMapping(value = {"/findProductByCode"})
    public Mono<ResponseEntity<APIResponse>> findProductByCode(@RequestBody ProductDTO productDTO)  {
        return lifeInsuranceService.findProductsByCode(productDTO);
    }
}
