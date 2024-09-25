package com.egroupx.esure.model.responses.kyc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CitizenResponse {

    private CitizenResponseBody response;
    private String title;
    private int status;
    private String detail;
    private String instance;

}
