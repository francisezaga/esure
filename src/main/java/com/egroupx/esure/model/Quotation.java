package com.egroupx.esure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quotation {

    private Long id;
    private Long fspQuoteRefId;
    private String categoryId;
    private String status;
    private List<PolicyHolder> policyHolders;
    private List<AllRisks> allRisks;
    private List<Building> buildings;
    private List<HouseHoldContents> householdContents;
    private List<MotorVehicles> motorVehicles;
}
