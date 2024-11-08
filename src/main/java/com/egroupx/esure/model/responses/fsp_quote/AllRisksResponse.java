package com.egroupx.esure.model.responses.fsp_quote;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllRisksResponse {

    private Long id;
    @JsonAlias("item-description")
    private String itemDescription;
    @JsonAlias("sum.insured")
    private String sumInsured;
    @JsonAlias("cover.type.id")
    private String coverTypeId;
}
