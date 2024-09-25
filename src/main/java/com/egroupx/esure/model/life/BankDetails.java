package com.egroupx.esure.model.life;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankDetails {

    private String client;
    private String policyNumber;
    private String sessionUserID;
    private String branchCode;
    private String accNumber;
    private String accType;
    private String accName;
    private String dedDay;
    private LocalDate fromDate;
    private String idNumber;
    private String subNaedo;

}
