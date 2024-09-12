package com.egroupx.esure.model.responses.fsp_quote;

import com.fasterxml.jackson.annotation.JsonAlias;
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
    @JsonAlias("car-colour")
    private String carColour;
    @JsonAlias("metalic-paint")
    private String metallicPaint;
    @JsonAlias("quotation-basis")
    private String quotationBasis;
    @JsonAlias("alarm-type-id")
    private String alarmTypeId;
    @JsonAlias("alarm-by-vesa")
    private String alarmByVesa;
    @JsonAlias("tracing-device")
    private String tracingDevice;
    @JsonAlias("short-term")
    private MotorVechicleShortTermResponse motorVechicleShortTerm;
    @JsonAlias("sh-link")
    private ShLinkResponse[] shLinks;
}
