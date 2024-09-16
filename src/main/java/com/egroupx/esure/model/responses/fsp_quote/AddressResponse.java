package com.egroupx.esure.model.responses.fsp_quote;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    @JsonAlias("type-cd")
    private String typeCd;
    @JsonAlias("line-1")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String line1;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String code;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String suburb;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @JsonAlias("residential-area-type")
    private String residentialAreaType;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String number;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @JsonAlias("is-cellphone")
    private String isCellphone;
    @JsonAlias("is-telephone")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String isTelephone;
    @JsonAlias("is-business")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String isBusiness;
    @JsonAlias("is-residential")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String isResidential;

}
