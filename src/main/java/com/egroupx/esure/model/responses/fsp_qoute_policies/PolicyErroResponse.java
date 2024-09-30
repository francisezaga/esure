package com.egroupx.esure.model.responses.fsp_qoute_policies;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyErroResponse {

    private List<PolicyErrors> errors;
}
