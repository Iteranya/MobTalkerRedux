package org.arsparadox.mobtalkerredux.vn.controller.vnmodules;

import org.arsparadox.mobtalkerredux.vn.controller.VisualNovelEngine;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class VariableHandler {

    public static void initializeVariable(VisualNovelEngine vn) {
        if(vn.localSave ==null){
            vn.localSave = new ArrayList<>();
            vn.localVariables.put("type","variable");
            vn.localSave.add(vn.localVariables);
            System.out.println("Initialize Local Variable");
        }else{
            // Copy all entries from the last save map to variables
            vn.localVariables.clear();
            vn.localVariables.putAll(vn.localSave.get(vn.localSave.size() - 1)); // OH SO THAT'S WHY THE REFERENCE IS DIFFERENT!!!

            if(vn.localVariables.get("checkpoint")!=null && !((String) vn.localVariables.get("checkpoint")).isEmpty()){
                vn.currentState.set(StateHandler.findLabelId((String) vn.localVariables.get("checkpoint"),vn.gameData));
            }
        }
        if(vn.globalSave ==null){
            vn.globalSave = new ArrayList<>();
            vn.globalVariables.put("type","variable");
            vn.globalSave.add(vn.globalVariables);
            System.out.println("Initialize Global Variable");
        }else{
            // Copy all entries from the last save map to variables
            vn.globalVariables.clear();
            vn.globalVariables.putAll(vn.globalSave.get(vn.globalSave.size() - 1)); // OH SO THAT'S WHY THE REFERENCE IS DIFFERENT!!!
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
