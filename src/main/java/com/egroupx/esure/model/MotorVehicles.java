package com.egroupx.esure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotorVehicles {
    private Long id;
    private Long fspQuoteRefId;
    private int year;
    private String make;
    private String model;
    private String carColour;
    private String metallicPaint;
    private String quotationBasis;
    private String alarmTypeId;
    private String alarmByVesa;
    private String tracingDevice;
    private List<SHLink> shLinks;
    private MotorVehicleShortTerm motorVehicleShortTerm;
}
