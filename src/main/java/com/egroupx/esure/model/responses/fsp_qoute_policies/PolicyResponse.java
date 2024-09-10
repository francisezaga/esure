package com.egroupx.esure.model.responses.fsp_qoute_policies;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyResponse {

    @JsonProperty("id")
    private String id;
    @JsonProperty("ver")
    private String ver;
    @JsonProperty("BTS-discount")
    private String btsDiscount;
    @JsonProperty("status")
    private String status;
    @JsonProperty("quoted-premium")
    private String quotePremium;
    @JsonProperty("date-quoted")
    private String dateQouted;
    @JsonProperty("category-id")
    private String categoryId;
    @JsonProperty("is-annual")
    private String isAnnual;
    @JsonProperty("allow-custom-broker-fee")
    private String allowCustomBrokerFee;
    @JsonProperty("offering-id")
    private String offeringId;
    @JsonProperty("offering-name")
    private String offeringName;
    @JsonProperty("quotation-id")
    private String quotationId;
    @JsonProperty("insurer-id")
    private String insurerId;
    @JsonProperty("insurer-name")
    private String insurerName;
    @JsonProperty("error-status")
    private String errorStatus;
    @JsonProperty("AllRisks")
    private QuoteResultResponse[] allRisks;
    @JsonProperty("Buildings")
    private QuoteResultResponse[] buildings;
    @JsonProperty("HouseholdContents")
    private QuoteResultResponse[] householdContents;
    @JsonProperty("MotorVehicles")
    private QuoteResultResponse[] motorVehicles;
    @JsonProperty("error")
    private ErrorResponse[] errors;
    @JsonProperty("requirement")
    private Object[] requirement;
}
