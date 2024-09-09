package com.egroupx.esure.model.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotorVechicleShortTerm {

    private String coverType;
    private String useTypeId;
    private String flatExcess;
    private String overnightParkingCd;
    private String overnightParkingTypeLocked;
}
