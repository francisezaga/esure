package com.egroupx.esure.exceptions;

import com.egroupx.esure.model.responses.life.LifeAPIResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

public class LifeAPIErrorHandler {

    private final static Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public static String handleLifeAPIError(String errMsg){
        String errorMsg = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            LifeAPIResponse lifeAPIResponse = objectMapper.readValue(errMsg, LifeAPIResponse.class);
            errorMsg = lifeAPIResponse.getMessage();
        }
        catch(Exception exp){
            LOG.error(MessageFormat.format("Could not transform API error response {0} ", exp.getMessage()));
        }
        return errorMsg;
    }

}
