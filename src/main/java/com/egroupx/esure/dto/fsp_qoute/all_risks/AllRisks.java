package com.egroupx.esure.dto.fsp_qoute.all_risks;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllRisks {

    @JsonAlias("itemDescription")
    @JsonProperty("item-description")
    private String itemDescription;
    @JsonAlias("sumInsured")
    @JsonProperty("sum.insured")
    private String sumInsured;
    @JsonAlias("coverTypeId")
    @JsonProperty("cover.type.id")
    private String coverTypeId;
}
