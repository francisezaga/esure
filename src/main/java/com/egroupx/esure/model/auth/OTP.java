package com.egroupx.esure.model.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OTP {

    private Long id;
    private String cellNumber;
    private LocalDateTime requestTime;
    private String idNumber;
    private int count;
    private String otpCode;

}
