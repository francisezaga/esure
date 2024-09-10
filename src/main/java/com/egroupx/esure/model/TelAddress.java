package com.egroupx.esure.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelAddress {

    @JsonAlias("typeCd")
    @JsonProperty("type-cd")
    private String typeCd;
    private String code;
    private String number;
    @JsonAlias("isCellphone")
    @JsonProperty("is-cellphone")
    private String isCellphone;
    @JsonAlias("isTelephone")
    @JsonProperty("is-telephone")
    private String isTelephone;
    @JsonAlias("isBusiness")
    @JsonProperty("is-business")
    private String isBusiness;
    @JsonAlias("isResidential")
    @JsonProperty("is-residential")
    private String isResidential;
}
