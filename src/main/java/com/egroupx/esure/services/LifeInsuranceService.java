package com.egroupx.esure.services;

import com.egroupx.esure.repository.LifeInsuranceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class LifeInsuranceService {

    @Value("${egroupx.services.fspEndpointUrl}")
    private String fspEndpointUrl;

    @Value("${egroupx.services.fspAPIKey:}")
    private String fspAPIKey;

    private WebClient webClient;

    private final LifeInsuranceRepository lifeInsuranceRepository;

    public LifeInsuranceService(LifeInsuranceRepository lifeInsuranceRepository) {
        this.lifeInsuranceRepository = lifeInsuranceRepository;
    }
}
