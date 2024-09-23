package com.egroupx.esure.exceptions;


import com.egroupx.esure.model.responses.api.APIResponse;
import com.egroupx.esure.model.responses.fsp_qoute_policies.QuotationResultResponse;
import com.egroupx.esure.model.responses.life.LifeAPIResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.MessageFormat;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(LifeAPIErrorException.class)
    public APIResponse handleRuntimeException(LifeAPIErrorException ex) {
        String errMsg = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            LifeAPIResponse lifeAPIResponse = objectMapper.readValue(ex.getMessage(), LifeAPIResponse.class);
            errMsg = lifeAPIResponse.getMessage();
        }
        catch(Exception exp){
            LOG.error(MessageFormat.format("Could not transform API error response {0} ", exp.getMessage()));
        }
        return new APIResponse(400,"error","Bad request" + errMsg, Instant.now());
    }
}
