package com.egroupx.esure.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quotation {

    @JsonAlias({"categoryId","Category_Id"})
    private int categoryId;
    @JsonAlias({"policyHolder","PolicyHolder"})
    private PolicyHolder[] policyHolder;
    @JsonAlias({"allRisks","AllRisks"})
    private AllRisks[] allRisks;
    @JsonAlias({"buildings","Buildings"})
    private Buildings[] buildings;
    @JsonAlias({"householdContents","HouseholdContents"})
    private HouseholdContents[] householdContents;
    @JsonAlias({"motorVehicles","MotorVehicles"})
    private MotorVehicles[] motorVehicles;
}
