package com.egroupx.esure.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private Long id;
    private String typeCd;
    private String line_1;
    private String code;
    private String suburb;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String residentialAreaType;

}
