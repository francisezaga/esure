package com.egroupx.esure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    private String name;
    private String fullNames;
    private String surname;
    private String initials;
    private String idNumber;
    private String titleCd;
    private String genderCd;
    private String maritalStatusCd;
    private String idType;
}
