package com.egroupx.esure.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailAddress {

    @JsonAlias({"typeCd","type-cd"})
    private String typeCd;
    @JsonAlias({"line1","line-1"})
    private String line1;
}
