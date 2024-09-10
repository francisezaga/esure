package com.egroupx.esure.dto.fsp_qoute;

import com.egroupx.esure.dto.fsp_qoute.all_risks.AllRisks;
import com.egroupx.esure.dto.fsp_qoute.building.Buildings;
import com.egroupx.esure.dto.fsp_qoute.house_hold.HouseholdContents;
import com.egroupx.esure.dto.fsp_qoute.personal_details.PolicyHolder;
import com.egroupx.esure.dto.fsp_qoute.vehicle.MotorVehicles;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quotation {

    @JsonAlias("categoryId")
    @JsonProperty("Category_Id")
    private int categoryId;
    @JsonAlias("policyHolder")
    @JsonProperty("PolicyHolder")
    private PolicyHolder[] policyHolder;
    @JsonAlias("allRisks")
    @JsonProperty("AllRisks")
    private AllRisks[] allRisks;
    @JsonAlias("buildings")
    @JsonProperty("Buildings")
    private Buildings[] buildings;
    @JsonAlias("householdContents")
    @JsonProperty("HouseholdContents")
    private HouseholdContents[] householdContents;
    @JsonAlias("motorVehicles")
    @JsonProperty("MotorVehicles")
    private MotorVehicles[] motorVehicles;
}
