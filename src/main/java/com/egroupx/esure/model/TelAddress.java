package com.egroupx.esure.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelAddress {

    @JsonAlias({"typeCd","type-cd"})
    private String typeCd;
    private String code;
    private String number;
    @JsonAlias({"isCellphone","is-cellphone"})
    private String isCellphone;
    @JsonAlias({"isTelephone","is-telephone"})
    private String isTelephone;
    @JsonAlias({"isBusiness","is-business"})
    private String isBusiness;
    @JsonAlias({"isResidential","is-residential"})
    private String isResidential;
}
