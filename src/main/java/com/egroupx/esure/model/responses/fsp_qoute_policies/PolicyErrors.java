package com.egroupx.esure.model.responses.fsp_qoute_policies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyErrors {

    private int status;
    private String title;
    private Object detail;
}
