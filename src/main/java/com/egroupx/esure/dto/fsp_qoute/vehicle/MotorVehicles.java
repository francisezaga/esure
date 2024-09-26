package com.egroupx.esure.dto.fsp_qoute.vehicle;

import com.egroupx.esure.dto.fsp_qoute.ShLink;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotorVehicles {
    private String year;
    private String make;
    private String model;
    @JsonAlias("carColour")
    @JsonProperty("car-colour")
    private String carColour;
    @JsonAlias("metallicPaint")
    @JsonProperty("metalic-paint")
    private String metallicPaint;
    @JsonAlias("quotationBasis")
    @JsonProperty("quotation-basis")
    private String quotationBasis;
    @JsonAlias("alarmTypeId")
    @JsonProperty("alarm-type-id")
    private String alarmTypeId;
    @JsonAlias("alarmByVesa")
    @JsonProperty("alarm-by-vesa")
    private String alarmByVesa;
    @JsonAlias("tracingDevice")
    @JsonProperty("tracing-device")
    private String tracingDevice;
    @JsonAlias("shortTerm")
    @JsonProperty("short-term")
    private MotorVechicleShortTerm motorVechicleShortTerm;
    @JsonAlias("shLinks")
    @JsonProperty("sh-link")
    private List<ShLink> shLinks;
}
