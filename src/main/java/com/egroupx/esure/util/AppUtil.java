package com.egroupx.esure.util;

import org.springframework.beans.BeanUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class AppUtil {

    private AppUtil(){}

    public static <E,T> T entityToDto(E entityRefObject,T dtoClass) {
        BeanUtils.copyProperties(entityRefObject, dtoClass);
        return dtoClass;
    }
    public static <E,T> E dtoToEntity(T dtoRefObject,E entityClass) {
        BeanUtils.copyProperties(dtoRefObject, entityClass);
        return entityClass;
    }

    public static LocalDate formatDate(String strDate){
        try{
            LocalDate date = null;
            if(strDate.contains("/")){
                Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(strDate.replaceAll("/","-"));
                date = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
            if(strDate.contains("-")) {
                Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(strDate);
                date = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
            return date;
        }
        catch(ParseException psEx){
           return null;
        }
    }
}
