package org.arsparadox.mobtalkerredux.vn.controller.vnmodules;

import org.arsparadox.mobtalkerredux.vn.controller.VisualNovelEngine;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class VariableHandler {

    public static void initializeVariable(VisualNovelEngine vn) {
        if(vn.saves==null){
            vn.saves = new ArrayList<>();
            vn.variables.put("type","variable");
            vn.saves.add(vn.variables);
            System.out.println("Initialize Variable");
        }else{
            // Copy all entries from the last save map to variables
            vn.variables.clear();
            vn.variables.putAll(vn.saves.get(vn.saves.size() - 1)); // OH SO THAT'S WHY THE REFERENCE IS DIFFERENT!!!

            if(vn.variables.get("checkpoint")!=null && !((String) vn.variables.get("checkpoint")).isEmpty()){
                vn.currentState.set(StateHandler.findLabelId((String) vn.variables.get("checkpoint"),vn.gameData));
            }
        }
    }

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
