package com.egroupx.esure.model.responses.fsp_quote;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonResponse {
    @JsonProperty("full-names")
    private String fullNames;
    private String surname;
    private String initials;
    @JsonProperty("identification-no")
    private String idNumber;
    @JsonProperty("title-cd")
    private String titleCd;
    @JsonProperty("gender-cd")
    private String genderCd;
    @JsonProperty("birth-date")
    private String birthDate;
    @JsonProperty("marital-status-cd")
    private String maritalStatusCd;
    @JsonProperty("id-type")
    private String idType;
    @JsonProperty("passport-number")
    private String passportNumber;
}
