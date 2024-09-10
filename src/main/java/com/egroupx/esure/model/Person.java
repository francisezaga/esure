package com.egroupx.esure.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    private String name;
    @JsonAlias("fullNames")
    @JsonProperty("full-names")
    private String fullNames;
    private String surname;
    private String initials;
    @JsonAlias("idNumber")
    @JsonProperty("identification-no")
    private String idNumber;
    @JsonAlias("titleCd")
    @JsonProperty("title-cd")
    private String titleCd;
    @JsonAlias("genderCd")
    @JsonProperty("gender-cd")
    private String genderCd;
    @JsonAlias("maritalStatusCd")
    @JsonProperty("marital-status-cd")
    private String maritalStatusCd;
    @JsonAlias("idType")
    @JsonProperty("id-type")
    private String idType;
}
