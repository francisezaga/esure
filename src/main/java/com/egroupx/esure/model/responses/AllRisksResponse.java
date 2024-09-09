package com.egroupx.esure.model.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllRisksResponse {

    private Long id;
    private String itemDescription;
    private String sumInsured;
    private String coverTypeId;
}
