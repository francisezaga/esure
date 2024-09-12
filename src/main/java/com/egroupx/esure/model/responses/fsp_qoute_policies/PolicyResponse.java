package com.egroupx.esure.model.responses.fsp_qoute_policies;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyResponse {

    private String id;
    private String ver;
    @JsonAlias("BTS-discount")
    private String btsDiscount;
    private String status;
    @JsonAlias("quoted-premium")
    private String quotePremium;
    @JsonAlias("date-quoted")
    private String dateQouted;
    @JsonAlias("category-id")
    private String categoryId;
    @JsonAlias("is-annual")
    private String isAnnual;
    @JsonAlias("allow-custom-broker-fee")
    private String allowCustomBrokerFee;
    @JsonAlias("offering-id")
    private String offeringId;
    @JsonAlias("offering-name")
    private String offeringName;
    @JsonAlias("quotation-id")
    private String quotationId;
    @JsonAlias("insurer-id")
    private String insurerId;
    @JsonAlias("insurer-name")
    private String insurerName;
    @JsonAlias("error-status")
    private String errorStatus;
    @JsonAlias("AllRisks")
    private QuoteResultResponse[] allRisks;
    @JsonAlias("Buildings")
    private QuoteResultResponse[] buildings;
    @JsonAlias("HouseholdContents")
    private QuoteResultResponse[] householdContents;
    @JsonAlias("MotorVehicles")
    private QuoteResultResponse[] motorVehicles;
    @JsonAlias("error")
    private ErrorResponse[] errors;
    @JsonAlias("requirement")
    private Object[] requirements;
}
