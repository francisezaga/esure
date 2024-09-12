package com.egroupx.esure.model.responses.fsp_quote;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonResponse {
    @JsonAlias("full-names")
    private String fullNames;
    private String surname;
    private String initials;
    @JsonAlias("identification-no")
    private String idNumber;
    @JsonAlias("title-cd")
    private String titleCd;
    @JsonAlias("gender-cd")
    private String genderCd;
    @JsonAlias("birth-date")
    private String birthDate;
    @JsonAlias("marital-status-cd")
    private String maritalStatusCd;
    @JsonAlias("id-type")
    private String idType;
    @JsonAlias("passport-number")
    private String passportNumber;
}
