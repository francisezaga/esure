package com.egroupx.esure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleInsuranceQuotationDTO {

    private int year;
    private String make;
    private String model;
    private String description;

}
