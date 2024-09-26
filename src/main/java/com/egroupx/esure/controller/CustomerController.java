package com.egroupx.esure.controller;


import com.egroupx.esure.model.responses.api.APIResponse;
import com.egroupx.esure.services.CustomerService;
import com.egroupx.esure.services.KYCVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/esure/customer")
public class CustomerController {

    private final CustomerService customerService;

    private final KYCVerificationService kycVerificationService;

    public CustomerController(CustomerService customerService, KYCVerificationService kycVerificationService) {
        this.customerService = customerService;
        this.kycVerificationService = kycVerificationService;
    }

    @GetMapping(value = {"/getAddressSuburbs"})
    public Mono<ResponseEntity<APIResponse>> getAddressSuburbs(@RequestParam String suburbName)  {
        return customerService.getAddressSuburbs(suburbName);
    }

    @GetMapping(value = {"/getCustomerStep/{idNumber}"})
    public Mono<ResponseEntity<APIResponse>> getCustomerStep(@PathVariable String idNumber)  {
        return customerService.getCustomerStep(idNumber);
    }

    @PostMapping(value = {"/submitCustomerDetails/{quoteRefId}"})
    public Mono<ResponseEntity<APIResponse>> submitMemberDetails(@PathVariable Long quoteRefId)  {
        return kycVerificationService.verifyPersonalDetailsForInsurance(quoteRefId);
    }
}
