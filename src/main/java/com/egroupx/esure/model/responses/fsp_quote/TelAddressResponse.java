package com.egroupx.esure.model.responses.fsp_quote;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelAddressResponse {

    @JsonAlias("type-cd")
    private String typeCd;
    private String code;
    private String number;
    @JsonAlias("is-cellphone")
    private String isCellphone;
    @JsonAlias("is-telephone")
    private String isTelephone;
    @JsonAlias("is-business")
    private String isBusiness;
    @JsonAlias("is-residential")
    private String isResidential;
}
