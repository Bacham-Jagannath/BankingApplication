package com.cg.admin.config;

import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDateTime;

@Component
public class AuditLoggingInterceptor implements Interceptor, Serializable {

    private static final String CREATED_ON = "createdOn";
    private static final String UPDATED_ON = "updatedOn";

    @Override
    public boolean onSave(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        boolean found = false;
        for (int i = 0; i < propertyNames.length; i++) {
            if(CREATED_ON.equalsIgnoreCase(propertyNames[i]) && state[i]==null){
                state[i] = getDate();
                found = true;

            }else if(UPDATED_ON.equalsIgnoreCase(propertyNames[i]) && state[i]==null){
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
            if(CREATED_ON.equalsIgnoreCase(propertyNames[i])){
                currentState[i] = getDate();
                found = true;

            }else if(UPDATED_ON.equalsIgnoreCase(propertyNames[i])){
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
