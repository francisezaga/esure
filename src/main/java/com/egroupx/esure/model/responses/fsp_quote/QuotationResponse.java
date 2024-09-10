package com.egroupx.esure.model.responses.fsp_quote;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class QuotationResponse {
    @JsonProperty("Id")
    private Long id;
    @JsonProperty("Category_Id")
    private int categoryId;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("PolicyHolder")
    private PolicyHolderResponse[] policyHolder;
    @JsonProperty("AllRisks")
    private AllRisksResponse[] allRisks;
    @JsonProperty("Buildings")
    private BuildingsResponse[] buildings;
    @JsonProperty("HouseholdContents")
    private HouseholdContentsResponse[] householdContents;
    @JsonProperty("MotorVehicles")
    private MotorVehiclesResponse[] motorVehicles;
}
