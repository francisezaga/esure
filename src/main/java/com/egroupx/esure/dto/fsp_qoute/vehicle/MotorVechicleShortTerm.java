package com.egroupx.esure.dto.fsp_qoute.vehicle;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotorVechicleShortTerm {

    @JsonAlias("coverType")
    @JsonProperty("cover-type")
    private String coverType;
    @JsonAlias("useTypeId")
    @JsonProperty("use-type-id")
    private String useTypeId;
    @JsonAlias("flatExcess")
    @JsonProperty("flat-excess")
    private String flatExcess;
    @JsonAlias("overnightParkingCd")
    @JsonProperty("overnight-parking-cd")
    private String overnightParkingCd;
    @JsonAlias("overnightParkingTypeLocked")
    @JsonProperty("overnight-parking-type-locked")
    private String overnightParkingTypeLocked;
}
