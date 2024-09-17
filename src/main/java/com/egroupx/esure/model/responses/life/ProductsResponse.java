package com.egroupx.esure.model.responses.life;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductsResponse {

    @JsonAlias("Result")
    private String result;
    @JsonAlias("Products")
    private List<Product> products;

}
