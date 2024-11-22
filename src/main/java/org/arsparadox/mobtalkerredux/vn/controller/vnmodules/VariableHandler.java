package org.arsparadox.mobtalkerredux.vn.controller.vnmodules;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class VariableHandler {

    public static void modifyVariable(String variable, String operation, Object value,Map<String, Object> variables,AtomicLong currentState) {
        if (operation.equals("increment_var")) {
            if (variables.get(variable) instanceof Number && value instanceof Number) {
                double result = ((Number) variables.get(variable)).doubleValue() +
                        ((Number) value).doubleValue();
                variables.put(variable, result);
            }
        } else if (operation.equals("subtract_var")) {
            if (variables.get(variable) instanceof Number && value instanceof Number) {
                double result = ((Number) variables.get(variable)).doubleValue() -
                        ((Number) value).doubleValue();
                variables.put(variable, result);
            }
        } else {
            variables.put(variable, value);
        }
        currentState.incrementAndGet();
    }


    public static void createVariable(String varName, Object varInit,Map<String, Object> variables,AtomicLong currentState) {
        variables.put(varName, varInit);
        currentState.incrementAndGet();
    }
}
