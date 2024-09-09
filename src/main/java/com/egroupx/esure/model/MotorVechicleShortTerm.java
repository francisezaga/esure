package com.egroupx.esure.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotorVechicleShortTerm {

    @JsonAlias({"coverType","cover-type"})
    private String coverType;
    @JsonAlias({"useTypeId","use-type-id"})
    private String useTypeId;
    @JsonAlias({"flatExcess","flat-excess"})
    private String flatExcess;
    @JsonAlias({"overnightParkingCd","overnight-parking-cd"})
    private String overnightParkingCd;
    @JsonAlias({"overnightParkingTypeLocked","overnight-parking-type-locked"})
    private String overnightParkingTypeLocked;
}
