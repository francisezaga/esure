package com.egroupx.esure.dto.fsp_qoute.personal_details;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeDetails {

    @JsonAlias("occupationCategory")
    @JsonProperty("occupation-category")
    private String occupationCategory;
}
