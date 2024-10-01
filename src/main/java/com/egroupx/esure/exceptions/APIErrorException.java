package com.egroupx.esure.exceptions;

public class APIErrorException extends RuntimeException {
    public APIErrorException(String errorMessage) {
        super(errorMessage);
    }
}
