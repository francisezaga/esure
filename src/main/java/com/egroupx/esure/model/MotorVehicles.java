package com.egroupx.esure.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotorVehicles {
    private String year;
    private String make;
    private String model;
    @JsonAlias({"carColour","car-colour"})
    private String carColour;
    @JsonAlias({"metallicPaint","metalic-paint"})
    private String metallicPaint;
    @JsonAlias({"quotationBasis","quotation-basis"})
    private String quotationBasis;
    @JsonAlias({"alarmTypeId","alarm-type-id"})
    private String alarmTypeId;
    @JsonAlias({"alarmByVesa","alarm-by-vesa"})
    private String alarmByVesa;
    @JsonAlias({"tracingDevice","tracing-device"})
    private String tracingDevice;
    @JsonAlias({"motorVechicleShortTerm","short-term"})
    private MotorVechicleShortTerm motorVechicleShortTerm;
    @JsonAlias({"shLinks","sh-link"})
    private ShLink[] shLinks;
}
