package com.egroupx.esure.model.responses.fsp_quote;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllRisksResponse {

    private Long id;
    @JsonProperty("item-description")
    private String itemDescription;
    @JsonProperty("sum.insured")
    private String sumInsured;
    @JsonProperty("cover.type.id")
    private String coverTypeId;
}
