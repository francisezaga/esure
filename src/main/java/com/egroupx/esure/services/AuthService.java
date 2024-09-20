package com.egroupx.esure.services;

import com.egroupx.esure.dto.auth.SMSDTO;
import com.egroupx.esure.model.auth.OTP;
import com.egroupx.esure.model.responses.api.APIResponse;
import com.egroupx.esure.repository.AuthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

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

   public Mono<ResponseEntity<APIResponse>> verifyUserCellNumber(String cellNumber){
        final String otpCode = generateOTPCode();
        String message = "eSure otp: "+ otpCode;
        return sendSms(cellNumber,message).flatMap(apiRes->{
            if(apiRes.getStatus()==200){
                return saveOTPDetails(cellNumber.trim(),LocalDateTime.now(),"",otpCode.trim())
                        .flatMap(apiResponse -> {
                            if(apiRes.getStatus()==200){
                                return Mono.just(ResponseEntity.ok().body(apiResponse));
                            }else{
                                return Mono.just(ResponseEntity.badRequest().body(apiResponse));
                            }
                        });
            }
            else{
                if(cellNumber.trim().equalsIgnoreCase("27800000000")){
                    return saveOTPDetails(cellNumber.trim(),LocalDateTime.now(),"",otpCode.trim())
                            .flatMap(apiResponse -> {
                                if(apiResponse.getStatus()==200){
                                    return Mono.just(ResponseEntity.ok().body(apiResponse));
                                }else{
                                    return Mono.just(ResponseEntity.badRequest().body(apiResponse));
                                }
                            });
                }else{
                    return Mono.just(ResponseEntity.badRequest().body(apiRes));
                }
            }
        });

   }
    private String generateOTPCode(){
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(100000);
        return String.format("%05d", num);
    }

    public Mono<ResponseEntity<APIResponse>> verifyOTP(String cellNumber,String otpCode){
        return getOTPDetails(cellNumber,otpCode).flatMap(apiRes->{
            if(apiRes.getStatus()==200){
                return Mono.just(ResponseEntity.ok().body(apiRes));
            }else{
                return Mono.just(ResponseEntity.badRequest().body(apiRes));
            }
        });
    }

    public Mono<APIResponse> sendSms(String cellNumber, String msg) {

        if (cellNumber != null && msg != null) {
            LOG.error("CellNumber = [".concat(cellNumber).concat("]").concat(" === ").concat("Msg = [").concat(msg).concat("]"));
            setConfigs(urlSMSEndpoint);

            SMSDTO smsDTO = new SMSDTO();
            smsDTO.setTo(cellNumber);
            smsDTO.setBody(msg);

            return authRepository.getOTPRequestDetails(cellNumber).flatMap(otpObj-> {
                //validate otp
                int count = 0;
                if(otpObj!=null && otpObj.getRequestTime()!=null) {
                    count = otpObj.getCount();
                }
                if(count<=3) {
                    return sendOPT(smsDTO);
                }else{
                    LOG.error(MessageFormat.format("Failed to send otp. Error {0}", "Too many request received"));
                    return Mono.just(new APIResponse(400, "fail", "Failed to send otp. Too many request received. Please try again later", Instant.now()));
                }
            }).switchIfEmpty(
                    Mono.just("next").flatMap(next->{
                        return sendOPT(smsDTO);
                            })
                    .onErrorResume(error->{
                LOG.error(MessageFormat.format("Failed to retrieve otp details. Error {0}", error.getMessage()));
                return Mono.just(new APIResponse(400, "fail", "Failed to retrieve otp details", Instant.now()));
            }));
        }else{
            return Mono.just(new APIResponse(400,"fail","OPT sms could not be send",Instant.now()));
        }
    }

    Mono<APIResponse> saveOTPDetails(String cellNumber, LocalDateTime requestTime, String idNumber, String otpCode) {
        return authRepository.getOTPRequestDetails(cellNumber).flatMap(otpObj-> {
            //validate otp
            int count = 0;
            if(otpObj!=null && otpObj.getRequestTime()!=null) {
                count = otpObj.getCount();
                LocalDateTime otpExpiry = otpObj.getRequestTime().plus(5, ChronoUnit.MINUTES);
                if(LocalDateTime.now().isBefore(otpExpiry)){
                    count = count+1;
                }
                if(LocalDateTime.now().isAfter(otpExpiry)){
                    count = 1;
                }
            }
            if(count<=3) {
                return authRepository.saveOTPRequestDetails(cellNumber, requestTime, idNumber, count, otpCode).then(Mono.just("next"))
                        .flatMap(msg -> {
                            LOG.info(MessageFormat.format("Completed saving otp details {0}", cellNumber));
                            return Mono.just(new APIResponse(200, "success", "OTP details successfully saved", Instant.now()));
                        }).onErrorResume(err -> {
                            LOG.error(MessageFormat.format("Failed to save otp details. Error {0}", err.getMessage()));
                            return Mono.just(new APIResponse(400, "fail", "Failed to save otp details", Instant.now()));
                        });
            }else{
                LOG.error(MessageFormat.format("Failed to save otp details. Error {0}", "Too many request received"));
                return Mono.just(new APIResponse(400, "fail", "Failed to send otp. Too many request received.Please try again later.", Instant.now()));
            }
        }).onErrorResume(error->{
            LOG.error(MessageFormat.format("Failed to retrieve otp details. Error {0}", error.getMessage()));
            return Mono.just(new APIResponse(400, "fail", "Failed to retrieve otp details", Instant.now()));
        });
    }

    Mono<APIResponse> getOTPDetails(String cellNumber,String otpMsg) {
        return authRepository.getOTPRequestDetails(cellNumber)
                .flatMap(otpObj -> {
                    LOG.info(MessageFormat.format("Completed retrieving otp details {0}", cellNumber));
                    if(otpObj.getOtpCode().trim().equalsIgnoreCase(otpMsg.trim()) && otpObj.getRequestTime()!=null && LocalDateTime.now().isBefore(otpObj.getRequestTime().plus(5, ChronoUnit.MINUTES))) {
                        return Mono.just(new APIResponse(200, "success", "Cell number verified", Instant.now()));
                    }else{
                        return Mono.just(new APIResponse(400, "fail", "Cell number could not be verified", Instant.now()));
                    }
                }).switchIfEmpty(Mono.just("fail").flatMap(msg->{
                    return Mono.just(new APIResponse(400,"success",msg,Instant.now()));
                }))
                .onErrorResume(err -> {
                    LOG.error(MessageFormat.format("Failed to retrieve otp details. Error {0}", err.getMessage()));
                    return Mono.just(new APIResponse(400,"fail","Failed to get otp",Instant.now()));
                });
    }

    Mono<APIResponse> sendOPT(SMSDTO smsDTO){
     return webClient.post()
                .uri("")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, "*/*")
                .bodyValue(smsDTO)
                .retrieve()
                .toEntity(Object.class).map(responseEntity->{
                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                        return new APIResponse(200,"success","OTP sms successfully send",Instant.now());
                    }else{
                        LOG.error("OTP sms could not be send");
                        return new APIResponse(400,"fail","OPT sms could not be send",Instant.now());
                    }
                })
                .timeout(Duration.ofSeconds(2))
                .flatMap(Mono::just)
                .onErrorResume(error->{
                    LOG.error("OTP sms could not be send. Error "+ error.getMessage());
                    return Mono.just(new APIResponse(400,"fail","OPT sms could not be send",Instant.now()));
                });
    }
}
