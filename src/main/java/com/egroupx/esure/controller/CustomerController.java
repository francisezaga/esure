package com.egroupx.esure.controller;


import com.egroupx.esure.model.responses.api.APIResponse;
import com.egroupx.esure.services.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/esure/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping(value = {"/getAddressSuburbs"})
    public Mono<ResponseEntity<APIResponse>> getAddressSuburbs(@RequestParam String suburbName)  {
        return customerService.getAddressSuburbs(suburbName);
    }
}
