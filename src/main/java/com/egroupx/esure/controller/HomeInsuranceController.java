package com.egroupx.esure.controller;

import com.egroupx.esure.services.HomeInsuranceService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/esure/home")
public class HomeInsuranceController {

    private final HomeInsuranceService homeInsuranceService;

    public HomeInsuranceController(HomeInsuranceService homeInsuranceService) {
        this.homeInsuranceService = homeInsuranceService;
    }
}
