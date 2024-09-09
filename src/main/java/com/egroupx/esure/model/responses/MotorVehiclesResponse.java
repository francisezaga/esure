package com.egroupx.esure.model.responses;

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
    private String carColour;
    private String metallicPaint;
    private String quotationBasis;
    private String alarmTypeId;
    private String alarmByVesa;
    private String tracingDevice;
    private String description;
    private String isCommercial;
    private MotorVechicleShortTerm motorVechicleShortTerm;
    private ShLinkResponse[] shLinks;
}
