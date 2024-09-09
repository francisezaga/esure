package com.egroupx.esure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailAddress {

    private String typeCd;
    private String line1;
}
