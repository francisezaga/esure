package com.egroupx.esure.model.responses.fsp_qoute_policies;

import com.egroupx.esure.model.responses.fsp_quote.PolicyHolderResponse;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyResponse {

    @JsonAlias("Id")
    private String id;
    private String ver;
    @JsonAlias("BTS-discount")
    private String btsDiscount;
    @JsonAlias("Status")
    private String status;
    @JsonAlias("Broker_Code")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String brokerCode;
    @JsonAlias("ExternalPolicyNo")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String externalPolicyNo;
    @JsonAlias({"quoted-premium","QuotedPremium"})
    private String quotePremium;
    @JsonAlias({"date-quoted","DateQuoted"})
    private LocalDateTime dateQuoted;
    @JsonAlias({"category-id","Category_Id"})
    private String categoryId;
    @JsonAlias("is-annual")
    private String isAnnual;
    @JsonAlias("allow-custom-broker-fee")
    private String allowCustomBrokerFee;
    @JsonAlias({"offering-id","Offering_Id"})
    private String offeringId;
    @JsonAlias({"offering-name","Offering_Name"})
    private String offeringName;
    @JsonAlias({"quotation-id","Quotation_Id"})
    private String quotationId;
    @JsonAlias({"insurer-id","Insurer_Id"})
    private String insurerId;
    @JsonAlias("insurer-name")
    private String insurerName;
    @JsonAlias({"error-status","ErrorStatus"})
    private String errorStatus;
    @JsonAlias("PolicyHolder")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private PolicyHolderResponse[] policyHolders;
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
    private RequirementResponse[] requirements;
}
