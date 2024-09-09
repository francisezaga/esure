package com.egroupx.esure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllRisks {

    private String itemDescription;
    private String sumInsured;
    private String coverTypeId;
}
