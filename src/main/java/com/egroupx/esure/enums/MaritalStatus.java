package com.egroupx.esure.enums;

import java.util.Arrays;

public enum MaritalStatus {

    Single("Single"),
    Cohabitating("Co-habitating"),
    Married("Married"),
    Divorced("Divorced"),
    Separated("Separated"),
    Widowed("Widowed");

    private final String statusVal;

    MaritalStatus(String statusVal) {
        this.statusVal = statusVal;
    }

    public static MaritalStatus getMaritalStatus(String val){
        return Arrays.stream(MaritalStatus.values())
                .filter(e -> e.statusVal.equalsIgnoreCase(val))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Unsupported marital status type %s.", val)));
    }
}
