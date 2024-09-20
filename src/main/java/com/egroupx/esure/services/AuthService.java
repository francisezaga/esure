package com.egroupx.esure.services;

import com.egroupx.esure.dto.auth.SMSDTO;
import com.egroupx.esure.repository.AuthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;

@Service
public class AuthService {

    @Value("${egroupx.services.urlSMSEndpoint}")
    private String urlSMSEndpoint;

    private WebClient webClient;

    private final Logger LOG = LoggerFactory.getLogger(LifeInsuranceService.class);

    private final AuthRepository authRepository;

    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    private void setConfigs(String endpointUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(endpointUrl)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE,
                        "Content-Type", MediaType.APPLICATION_JSON_VALUE
                ).build();
    }

    public Mono<String> sendSms(String cellNumber, String msg) {

        if (cellNumber != null && msg != null) {
            LOG.error("CellNumber = [".concat(cellNumber).concat("]").concat(" === ").concat("Msg = [").concat(msg).concat("]"));
            setConfigs(urlSMSEndpoint);

            SMSDTO smsDTO = new SMSDTO();
            smsDTO.setTo(cellNumber);
            smsDTO.setBody(msg);

            return webClient.post()
                    .uri("")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.ACCEPT, "*/*")
                    .bodyValue(smsDTO)
                    .retrieve()
                    .toEntity(Object.class).map(responseEntity->{
                        if (responseEntity.getStatusCode().is2xxSuccessful()) {
                            return "OTP sms successfully send";
                        }else{
                            LOG.error("OTP sms could not be send");
                            return "OPT sms could not be send";
                        }
                    })
                    .flatMap(Mono::just)
                    .onErrorResume(error->{
                        LOG.error("OTP sms could not be send. Error "+ error.getMessage());
                        return Mono.just("OPT sms could not be send");
                    });

        }else{
            return Mono.just("OPT sms could not be send");
        }
    }

    Mono<String> saveOTPDetails(String cellNumber, Instant requestTime, String idNumber, int count) {
        return authRepository.saveOTPRequestDetails(cellNumber,requestTime,idNumber,count).then(Mono.just("next"))
                .flatMap(msg -> {
                    LOG.info(MessageFormat.format("Completed saving otp details {0}", cellNumber));
                    return Mono.just("OTP details successfully saved");
                }).onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to save otp details. Error {0}", err.getMessage()));
                    return Mono.just("Failed to save otp details");
                });
    }
}
