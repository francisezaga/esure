package com.egroupx.esure.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OTP {

    private Long id;
    private String cellNumber;
    private Instant requestTime;
    private String idNumber;
    private int count;

}
