package com.egroupx.esure.exceptions;

import com.egroupx.esure.model.responses.fsp_qoute_policies.PolicyErroResponse;
import com.egroupx.esure.model.responses.life.LifeAPIResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

public class APIErrorHandler {

    private final static Logger LOG = LoggerFactory.getLogger(APIErrorHandler.class);

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

    public static String handleFSPAPIError(String errMsg){
        String errorMsg = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            PolicyErroResponse policyErroResponse = objectMapper.readValue(errMsg, PolicyErroResponse.class);
            errorMsg = policyErroResponse.getErrors().get(0).getTitle();
        }
        catch(Exception exp){
            LOG.error(MessageFormat.format("Could not transform API error response {0} ", exp.getMessage()));
        }
        return errorMsg;
    }

    public static int handleFSPAPIErrorCode(String errMsg){
        int errorCode = 400;
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            PolicyErroResponse policyErroResponse = objectMapper.readValue(errMsg, PolicyErroResponse.class);
            errorCode = policyErroResponse.getErrors().get(0).getStatus();
        }
        catch(Exception exp){
            LOG.error(MessageFormat.format("Could not transform API error response {0} ", exp.getMessage()));
        }
        return errorCode;
    }

}
