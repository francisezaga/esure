package com.egroupx.esure.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private boolean isVerified;

}
