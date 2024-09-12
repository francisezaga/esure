package com.egroupx.esure.model.responses.fsp_quote;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotorVechicleShortTermResponse {

    @JsonAlias("cover-type")
    private String coverType;
    @JsonAlias("use-type-id")
    private String useTypeId;
    @JsonAlias("flat-excess")
    private String flatExcess;
    @JsonAlias("overnight-parking-cd")
    private String overnightParkingCd;
    @JsonAlias("overnight-parking-type-locked")
    private String overnightParkingTypeLocked;
}
