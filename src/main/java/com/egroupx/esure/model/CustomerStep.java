package com.egroupx.esure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerStep {

    private Long id;
    private Long fspQuoteRefId;
    private String idNumber;
    private String passportNumber;
    private String step;
}
