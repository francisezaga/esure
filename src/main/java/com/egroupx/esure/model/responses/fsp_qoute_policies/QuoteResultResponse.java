package com.egroupx.esure.model.responses.fsp_qoute_policies;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuoteResultResponse {

    private String id;
    private String ver;
    @JsonAlias("category-id")
    private String categoryId;
    @JsonAlias("description")
    private String description;
    @JsonAlias("insured-amount")
    private String insuredAmount;
    private String status;
    @JsonAlias("quoted-premium")
    private String quotedPremium;
    @JsonAlias("error")
    private ErrorResponse[] error;
    @JsonAlias("requirement")
    private Object[] requirement;

}
