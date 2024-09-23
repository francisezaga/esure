package com.egroupx.esure.exceptions;

public class LifeAPIErrorException extends RuntimeException {
    public LifeAPIErrorException(String errorMessage) {
        super(errorMessage);
    }
}
