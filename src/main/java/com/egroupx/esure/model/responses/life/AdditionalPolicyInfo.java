package com.egroupx.esure.model.responses.life;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalPolicyInfo {

    @JsonAlias("RelateTo")
    private String relateTo;
    @JsonAlias("AgeFrom")
    private String ageFrom;
    @JsonAlias("AgeTo")
    private String ageTo;
    @JsonAlias("Cover")
    private String cover;
    @JsonAlias("Premium")
    private String premium;
    @JsonAlias("AccCover")
    private String accCover;
    @JsonAlias("UnderWriterPremium")
    private String underWriterPremium;
}
