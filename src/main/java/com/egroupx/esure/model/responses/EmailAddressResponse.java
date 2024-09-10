package com.egroupx.esure.model.responses;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailAddressResponse {

    @JsonProperty("type-cd")
    private String typeCd;
    @JsonProperty("line-1")
    private String line1;
}
