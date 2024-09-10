package com.egroupx.esure.model.responses;

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
