package com.egroupx.esure.enums;

import java.util.Arrays;

public enum Gender {

    MALE("Male"),
    FEMALE("Female");

    private final String genderVal;

    Gender(String genderVal) {
        this.genderVal = genderVal;
    }

    public static Gender getGenderType(String val){
        return Arrays.stream(Gender.values())
                .filter(e -> e.genderVal.equalsIgnoreCase(val))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Unsupported Gender type %s.", val)));
    }
}
