package com.egroupx.esure.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelAddress {

    private String typeCd;
    private String code;
    private String number;
    private String isCellphone;
    private String isTelephone;
    private String isBusiness;
    private String isResidential;
}
