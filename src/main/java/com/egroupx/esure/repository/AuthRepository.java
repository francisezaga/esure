package com.egroupx.esure.repository;

import com.egroupx.esure.model.auth.OTP;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;

@Repository
public interface AuthRepository extends ReactiveCrudRepository<OTP,Long> {

    @Query("INSERT IGNORE INTO opt_auth_request SET cell_number=:cellNumber,request_time=:requestTime, id_number=:idNumber,count=:count ON DUPLICATE KEY UPDATE cell_number=:cellNumber,request_time=:requestTime, id_number=:idNumber,count=:count")
    Mono<OTP> saveOTPRequestDetails(String cellNumber, Instant requestTime, String idNumber, int count);

    @Query("SELECT * from opt_auth_request where cell_number=:cellNumber")
    Mono<OTP> getOTPRequestDetails(String cellNumber);

}
