package com.egroupx.esure.services;

import com.egroupx.esure.model.responses.life.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.util.Base64;
import java.util.UUID;


@Service
public class TokenService {

    @Value("${egroupx.services.pol360EndpointUrl}")
    private String pol360EndpointUrl;

    @Value("${egroupx.services.pol360TokenGenClientName}")
    private String pol360TokenGenClientName;

    @Value("${egroupx.services.pol360TokenUUID}")
    private String pol360TokenUUID;

    private final Logger LOG = LoggerFactory.getLogger(TokenService.class);

    private WebClient webClientPost;

    private void setConfigs(){

        this.webClientPost = WebClient.builder()
                .baseUrl(pol360EndpointUrl)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE,
                        "Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<String> getPol360APIToken(){
        setConfigs();
        return webClientPost.get()
                .uri("/api/360APITEST.php?Function=GenerateAuthToken&ClientName="+pol360TokenGenClientName)
                .header(HttpHeaders.ACCEPT, "*/*")
                .header("x-authorization-token", pol360TokenUUID)
                .retrieve()
                .toEntity(TokenResponse.class).map(responseEntity -> {
                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                        TokenResponse tokenRes = responseEntity.getBody();
                        if(tokenRes!=null && tokenRes.getJwtToken()!=null) {
                            return tokenRes.getJwtToken();
                        }else{
                            return "";
                        }
                    } else {
                        return "";
                    }
                }).onErrorResume(error->{
                    LOG.error(MessageFormat.format("Error getting token api token. Error {0}",error.getMessage()));
                    return Mono.just("");
                });
    }
}
