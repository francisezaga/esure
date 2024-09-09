package com.egroupx.esure.util;

import org.springframework.beans.BeanUtils;

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
}
