package com.egroupx.esure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quotation {

    private int Category_Id;
    private PolicyHolder[] PolicyHolder;
    private AllRisks[] AllRisks;
    private Buildings[] Buildings;
    private HouseholdContents[] HouseholdContents;
    private MotorVehicles[] MotorVehicles;
}
