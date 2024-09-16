package com.egroupx.esure.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelAddress {
    private Long id;
    private String typeCd;
    private String code;
    private String number;
    private String isCellphone;
    private String isTelephone;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String isBusiness;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String isResidential;
}
