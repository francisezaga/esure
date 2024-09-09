package com.egroupx.esure.controller;

import com.egroupx.esure.services.LifeInsuranceService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/esure/life")
public class LifeInsuranceController {

    private final LifeInsuranceService lifeInsuranceService;

    public LifeInsuranceController(LifeInsuranceService lifeInsuranceService) {
        this.lifeInsuranceService = lifeInsuranceService;
    }
}
