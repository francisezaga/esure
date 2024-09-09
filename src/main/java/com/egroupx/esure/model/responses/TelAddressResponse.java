package com.egroupx.esure.model.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelAddressResponse {

    private String typeCd;
    private String code;
    private String number;
    private String isCellphone;
    private String isTelephone;
    private String isbusiness;
    private String isResidential;
}
