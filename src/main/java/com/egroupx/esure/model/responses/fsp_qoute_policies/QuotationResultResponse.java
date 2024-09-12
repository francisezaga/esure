package com.egroupx.esure.model.responses.fsp_qoute_policies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuotationResultResponse {

    private Long id;
    private PolicyResponse[] policies;
    private PolicyLinksResponse _links;
}
