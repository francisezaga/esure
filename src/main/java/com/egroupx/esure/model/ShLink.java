package com.egroupx.esure.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShLink {

    private Long id;
    @JsonAlias({"linkTypeId","link-type-id"})
    private String linkTypeId;
}
