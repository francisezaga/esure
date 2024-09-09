package com.egroupx.esure.services;


import com.egroupx.esure.repository.HomeInsuranceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class HomeInsuranceService {

    @Value("${egroupx.services.fspEndpointUrl}")
    private String fspEndpointUrl;

    @Value("${egroupx.services.fspAPIKey:}")
    private String fspAPIKey;

    private WebClient webClient;

    private final HomeInsuranceRepository homeInsuranceRepository;

    public HomeInsuranceService(HomeInsuranceRepository homeInsuranceRepository) {
        this.homeInsuranceRepository = homeInsuranceRepository;
    }
}
