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

    @JsonAlias("client")
    @JsonProperty("Client")
    private String client;
    @JsonAlias("policyNumber")
    @JsonProperty("PolicyNumber")
    private String policyNumber;
    @JsonAlias("sessionUserId")
    @JsonProperty("SessionUserID")
    private String sessionUserID;
    @JsonAlias("branchCode")
    @JsonProperty("BranchCode")
    private String branchCode;
    @JsonAlias("accNumber")
    @JsonProperty("AccNumber")
    private String accNumber;
    @JsonAlias("accType")
    @JsonProperty("AccType")
    private String accType;
    @JsonAlias("accName")
    @JsonProperty("AccName")
    private String accName;
    @JsonAlias("dedDay")
    @JsonProperty("DedDay")
    private String dedDay;
    @JsonAlias("fromDate")
    @JsonProperty("fromDate")
    private LocalDate fromDate;
    @JsonAlias("idNumber")
    @JsonProperty("IDNumber")
    private String idNumber;
    @JsonAlias("subNaedo")
    @JsonProperty("SubNaedo")
    private String subNaedo;
    @JsonAlias("function")
    @JsonProperty("Function")
    private String function;

}
