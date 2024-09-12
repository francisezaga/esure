package com.egroupx.esure.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private Long id;
    private Long fspQuoteRefId;
    private Long fspPhRefId;
    private String surname;
    private String initials;
    private String fullNames;
    private String idNumber;
    private String titleCd;
    private String genderCd;
    private LocalDate birthDate;
    private String maritalStatusCd;
    private String idType;
    private String passportNumber;
}
