package com.egroupx.esure.model.responses.fsp_qoute_policies;

import com.egroupx.esure.model.responses.fsp_quote.CalculationsStatusResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyLinksResponse {

    private CalculationsStatusResponse status;
    private CalculationsStatusResponse recalculate;

}
