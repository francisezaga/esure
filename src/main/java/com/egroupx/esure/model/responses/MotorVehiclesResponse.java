package com.egroupx.esure.model.responses;

import com.egroupx.esure.model.MotorVechicleShortTerm;
import com.egroupx.esure.model.ShLink;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotorVehiclesResponse {
    private Long id;
    private String year;
    private String make;
    private String model;
    @JsonProperty("car-colour")
    private String carColour;
    @JsonProperty("metalic-paint")
    private String metallicPaint;
    @JsonProperty("quotation-basis")
    private String quotationBasis;
    @JsonProperty("alarm-type-id")
    private String alarmTypeId;
    @JsonProperty("alarm-by-vesa")
    private String alarmByVesa;
    @JsonProperty("tracing-device")
    private String tracingDevice;
    @JsonProperty("short-term")
    private MotorVechicleShortTerm motorVechicleShortTerm;
    @JsonProperty("sh-link")
    private ShLink[] shLinks;
}
