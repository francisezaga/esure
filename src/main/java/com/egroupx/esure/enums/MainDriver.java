package com.egroupx.esure.enums;

import java.util.Arrays;

public enum MainDriver {

    Me("Me"),
    SOMEONE_ELSE("Someone else");

    private final String driverVal;

    MainDriver(String driverVal) {
        this.driverVal = driverVal;
    }

    public static MainDriver getDriverType(String val){
        return Arrays.stream(MainDriver.values())
                .filter(e -> e.driverVal.equalsIgnoreCase(val))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Unsupported driver type %s.", val)));
    }
}
