package com.egroupx.esure.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String typeCd;
    private String line1;
    private String code;
    private String suburb;
    private String residentialAreaType;

}
