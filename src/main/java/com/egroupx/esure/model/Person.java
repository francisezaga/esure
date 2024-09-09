package com.egroupx.esure.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    private String name;
    @JsonAlias({"fullNames","full-names"})
    private String fullNames;
    private String surname;
    private String initials;
    @JsonAlias({"idNumber","identification-no"})
    private String idNumber;
    @JsonAlias({"titleCd","title-cd"})
    private String titleCd;
    @JsonAlias({"genderCd","gender-cd"})
    private String genderCd;
    @JsonAlias({"maritalStatusCd","marital-status-cd"})
    private String maritalStatusCd;
    @JsonAlias({"idType","id-type"})
    private String idType;
}
