package com.egroupx.esure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PHIncomeDetails {
    private Long id;
    private Long fspQuoteRefId;
    private Long fspPhRefId;
    private String occupationCategory;
}
