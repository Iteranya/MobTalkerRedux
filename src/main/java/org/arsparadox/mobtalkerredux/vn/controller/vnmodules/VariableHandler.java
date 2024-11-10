package org.arsparadox.mobtalkerredux.vn.controller.vnmodules;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class VariableHandler {

    public static void initializeVariable(List<Map<String, Object>> gameData, Map<String, Object> variables, AtomicLong currentState) {
        if(!"variable".equals(gameData.get(gameData.size() - 1).get("type"))){
            variables.put("type","variable");
            gameData.add(variables);
            System.out.println("Initialize Variable");
        }else{
            // Copy all entries from the last gameData map to variables
            variables.clear();
            variables.putAll(gameData.get(gameData.size() - 1));

            if(variables.get("checkpoint")!=null && !((String) variables.get("checkpoint")).isEmpty()){
                currentState.set(StateHandler.findLabelId((String) variables.get("checkpoint"),gameData));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void modifyVariable(String variable, String operation, Object value,Map<String, Object> variables,AtomicLong currentState) {
        if (operation.equals("increment_var")) {
            if (variables.get(variable) instanceof Number && value instanceof Number) {
                double result = ((Number) variables.get(variable)).doubleValue() +
                        ((Number) value).doubleValue();
                variables.put(variable, result);
            }
        } else if (operation.equals("substract_var")) {
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
