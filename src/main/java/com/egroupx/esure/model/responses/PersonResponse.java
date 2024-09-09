package com.egroupx.esure.model.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonResponse {
    private String name;
    private String fullNames;
    private String initials;
    private String idNumber;
    private String titleCd;
    private String genderCd;
    private String maritalStatusCd;
    private String idType;
    private Date birthDate;
    private String passportNumber;
}
