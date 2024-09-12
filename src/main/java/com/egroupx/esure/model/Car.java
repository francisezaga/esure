package com.egroupx.esure.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car {


    Long id;
    private int year;
    private String make;
    private String model;
    private String coverType;
    private String parkingNight;
    private Long customerId;

}
