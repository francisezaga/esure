package com.egroupx.esure.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quotation {

    private Long id;
    private Long fspRefId;
    private String categoryId;
    private String status;
}
