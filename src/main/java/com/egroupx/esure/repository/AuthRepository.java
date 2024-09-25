package com.egroupx.esure.repository;

import com.egroupx.esure.model.auth.AppUser;
import com.egroupx.esure.model.auth.OTP;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface AuthRepository extends ReactiveCrudRepository<OTP,Long> {

    @Query("INSERT IGNORE INTO esure_otp_auth_request SET cell_number=:cellNumber,request_time=:requestTime, id_number=:idNumber,count=:count,otp_code=:otpCode ON DUPLICATE KEY UPDATE cell_number=:cellNumber,request_time=:requestTime, id_number=:idNumber,count=:count,otp_code=:otpCode")
    Mono<OTP> saveOTPRequestDetails(String cellNumber, LocalDateTime requestTime, String idNumber, int count, String otpCode);

    @Query("SELECT * from esure_otp_auth_request where cell_number=:cellNumber order by id desc limit 1")
    Mono<OTP> getOTPRequestDetails(String cellNumber);

    @Query("UPDATE esure_otp_auth_request SET is_verified=:isVerified where cell_number=:cellNumber")
    Mono<OTP> updateOTPVerificationDetails(boolean isVerified,String cellNumber);

    @Query("SELECT * from esure_api_consumer_user where username=:userName")
    Mono<AppUser> findByUsername(String userName);

}
