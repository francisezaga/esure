package com.egroupx.esure.controller;


import com.egroupx.esure.model.Quotation;
import com.egroupx.esure.model.responses.APIResponse;

import com.egroupx.esure.services.QuotationService;
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

    @PostMapping(value = {"/requestQuotation"})
    public Mono<ResponseEntity<APIResponse>> requestQuotation(@RequestBody Quotation quotation)  {
        return quotationService.requestQuotation(quotation);
    }
}
