package com.egroupx.esure.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllRisks {

    @JsonAlias({"itemDescription","item-description"})
    private String itemDescription;
    @JsonAlias({"sumInsured","sum.insured"})
    private String sumInsured;
    @JsonAlias({"coverTypeId","cover.type.id"})
    private String coverTypeId;
}
