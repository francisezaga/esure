package com.egroupx.esure.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum DOCType {
    ID_SELFIE("ID_SELFIE"),
    PASSPORT("PASSPORT"),
    RSA_ID("RSA_ID"),
    OTHER("OTHER");

    private final String docTypeVal;

    DOCType(String val) {
        this.docTypeVal = val;
    }

    public static DOCType getDocType(String val){
        return Arrays.stream(DOCType.values())
                .filter(doc -> doc.getDocTypeVal().equalsIgnoreCase(val))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Unsupported document type %s.", val)));
    }
}