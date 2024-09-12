package com.egroupx.esure.model.responses.fsp_quote;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class QuotationResponse {
    @JsonAlias("Id")
    private Long id;
    @JsonAlias("Category_Id")
    private int categoryId;
    @JsonAlias("Status")
    private String status;
    @JsonAlias("PolicyHolder")
    private PolicyHolderResponse[] policyHolders;
    @JsonAlias("AllRisks")
    private AllRisksResponse[] allRisks;
    @JsonAlias("Buildings")
    private BuildingsResponse[] buildings;
    @JsonAlias("HouseholdContents")
    private HouseholdContentsResponse[] householdContents;
    @JsonAlias("MotorVehicles")
    private MotorVehiclesResponse[] motorVehicles;
}
