package com.egroupx.esure.controller;


import com.egroupx.esure.model.Quotation;
import com.egroupx.esure.model.responses.APIResponse;

import com.egroupx.esure.services.QuotationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/esure/quotation")
public class QuotationController {

    private final QuotationService quotationService;

    public QuotationController(QuotationService quotationService) {
        this.quotationService = quotationService;
    }

    @PostMapping(value = {"/requestQuotation"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<APIResponse>> requestQuotation(@RequestBody Quotation quotation)  {
        return quotationService.requestQuotation(quotation);
    }

    @PostMapping(value = {"/calculateQuotation/{quotationId}"})
    public Mono<ResponseEntity<APIResponse>> calculateQuotation(@PathVariable Long quotationId)  {
        return quotationService.calculateQuotation(quotationId);
    }

    @GetMapping(value = {"/getQuotationResult/{quotationId}"})
    public Mono<ResponseEntity<APIResponse>> getQuotationResult(@PathVariable Long quotationId)  {
        return quotationService.getQuotationResult(quotationId);
    }
}
