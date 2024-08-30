package com.cg.config;

import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDateTime;

@Component
public class AuditLoggingInterceptor implements Interceptor, Serializable {


    private static final String createdDate = "createdDate";
    private static final String updatedDate = "updatedDate";

    private static final String createdBy = "createdBy";

    private static final String updatedBy = "updatedBy";

    @Autowired
    HttpServletRequest httpServletRequest;

    @Override
    public boolean onSave(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        boolean found = false;
        for (int i = 0; i < propertyNames.length; i++) {
            if(createdDate.equalsIgnoreCase(propertyNames[i]) && state[i]==null){
                state[i] = getDate();
                found = true;

            }else if(updatedDate.equalsIgnoreCase(propertyNames[i]) && state[i]==null){
                state[i] = getDate();
                found = true;

            }
        }
        return found;
    }

    @Override
    public boolean onFlushDirty(Object entity, Object id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException {
        boolean found = false;
        for (int i = 0; i < propertyNames.length; i++) {
            if(createdDate.equalsIgnoreCase(propertyNames[i])){
                currentState[i] = getDate();
                found = true;

            }else if(updatedDate.equalsIgnoreCase(propertyNames[i])){
                currentState[i] = getDate();
                found = true;

            }
        }
        return found;
    }

    private LocalDateTime getDate(){
        return LocalDateTime.now();
    }
}
