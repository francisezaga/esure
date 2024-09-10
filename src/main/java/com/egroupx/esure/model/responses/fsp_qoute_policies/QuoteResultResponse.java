package com.egroupx.esure.model.responses.fsp_qoute_policies;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuoteResultResponse {

    @JsonProperty("id")
    private String id;
    @JsonProperty("ver")
    private String ver;
    @JsonProperty("category-id")
    private String categoryId;
    @JsonProperty("description")
    private String description;
    @JsonProperty("insured-amount")
    private String insuredAmount;
    @JsonProperty("status")
    private String status;
    @JsonProperty("quoted-premium")
    private String quotedPremium;
    @JsonProperty("error")
    private ErrorResponse[] error;
    @JsonProperty("requirement")
    private Object[] requirement;

}
