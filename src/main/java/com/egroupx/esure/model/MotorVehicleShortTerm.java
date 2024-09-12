package com.egroupx.esure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotorVehicleShortTerm {

    private Long id;
    private Long fspQuoteRefId;
    private String coverType;
    private String useTypeId;
    private String flatExcess;
    private String overnightParkingCd;
    private String overnightParkingTypeLocked;
}
