package com.egroupx.esure.model.responses;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotorVechicleShortTerm {

    @JsonProperty("cover-type")
    private String coverType;
    @JsonProperty("use-type-id")
    private String useTypeId;
    @JsonProperty("flat-excess")
    private String flatExcess;
    @JsonProperty("overnight-parking-cd")
    private String overnightParkingCd;
    @JsonProperty("overnight-parking-type-locked")
    private String overnightParkingTypeLocked;
}
