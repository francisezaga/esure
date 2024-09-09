package com.egroupx.esure.model.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class QuotationResponse {

    private Long id;
    private int Category_Id;
    private String Status;
    private PolicyHolderResponse[] PolicyHolder;
    private AllRisksResponse[] AllRisks;
    private BuildingsResponse[] Buildings;
    private HouseholdContentsResponse[] HouseholdContents;
    private MotorVehiclesResponse[] MotorVehicles;
}
