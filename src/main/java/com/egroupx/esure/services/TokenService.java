package com.egroupx.esure.services;

import com.egroupx.esure.dto.auth.Token;
import com.egroupx.esure.model.responses.life.TokenResponse;
import com.egroupx.esure.repository.ITokenRepository;
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

    @Value("${egroupx.services.marisit.url}")
    private String maristUrlPost;
    @Value("${egroupx.services.marisit.username}")
    private String user;
    @Value("${egroupx.services.marisit.password}")
    private String password;

    @Value("${egroupx.services.pol360.endpointUrl}")
    private String pol360EndpointUrl;

    @Value("${egroupx.services.pol360.tokenGenClientName}")
    private String pol360TokenGenClientName;

    @Value("${egroupx.services.pol360.tokenUUID}")
    private String pol360TokenUUID;

    private final ITokenRepository iTokenRepository;

    private final Logger LOG = LoggerFactory.getLogger(TokenService.class);

    private WebClient webClientPost;

    public TokenService(ITokenRepository iTokenRepository) {
        this.iTokenRepository = iTokenRepository;
    }

    private void setConfigs(String endpointBaseUrl){

        this.webClientPost = WebClient.builder()
                .baseUrl(endpointBaseUrl)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE,
                        "Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<String> getPol360APIToken(){
        setConfigs(pol360EndpointUrl);
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

    public Mono<String> getMaristToken(){
        setConfigs(maristUrlPost);
        Token tokenReq = new Token();
        tokenReq.setUsername(this.user);
        tokenReq.setPassword(this.password);
        return webClientPost.post()
                .uri("api/v6/login")
                .bodyValue(tokenReq)
                .retrieve()
                .toEntity(String.class).map(responseEntity -> {
                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                        String token = responseEntity.getBody();
                        return token.replaceAll("\"","");
                    } else {
                        return "";
                    }
                });
    }

    public Mono<String> getCitizenAPIToken(){
        return iTokenRepository.findCitizenAPIToken();
        //return Mono.just("");
    }
}
