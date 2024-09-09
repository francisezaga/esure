package com.egroupx.esure.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Suburb {
        String id;
        String name;
        String streetCode;
        String boxCode;
        String city;
}
