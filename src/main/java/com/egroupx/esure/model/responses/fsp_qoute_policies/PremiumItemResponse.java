package com.egroupx.esure.model.responses.fsp_qoute_policies;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PremiumItemResponse {

    private String type;
    @JsonAlias("type-desc")
    private String typeDesc;
    private String amount;
    @JsonAlias("adjusted-amount")
    private String adjustedAmount;
    @JsonAlias("is-optional")
    private String isOptional;
    @JsonAlias("is-selected")
    private String isSelected;
    private String aid;
    private String pver;
}
