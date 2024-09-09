package com.egroupx.esure.model;

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
    private String carColour;
    private String metallicPaint;
    private String quotationBasis;
    private String alarmTypeId;
    private String alarmByVesa;
    private String tracingDevice;
    private MotorVechicleShortTerm motorVechicleShortTerm;
    private ShLink[] shLinks;
}
