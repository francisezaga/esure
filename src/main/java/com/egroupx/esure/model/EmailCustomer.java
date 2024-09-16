package com.egroupx.esure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailCustomer {

    private Long fspQuoteRefId;
    private Long fspPolicyId;
    private String fullNames;
    private String idNumber;
    private String code;
    private String number;
    private String line_1;
    private String instructions;
}
