package com.egroupx.esure.model.responses.fsp_qoute_policies;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
    @JsonAlias("BTS-discount")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String btsDiscount;
    @JsonAlias("qt-action")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String qtAction;
    @JsonAlias("qt-data")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String qtData;
    @JsonAlias("insured-amount")
    private String insuredAmount;
    private String status;
    @JsonAlias("quoted-premium")
    private String quotedPremium;
    @JsonAlias("actual-premium")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String actualPremium;
    @JsonAlias("error")
    private ErrorResponse[] error;
    @JsonAlias("requirement")
    private RequirementResponse[] requirement;
    @JsonAlias("premium-item")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private PremiumItemResponse[] premiumItem;

}
