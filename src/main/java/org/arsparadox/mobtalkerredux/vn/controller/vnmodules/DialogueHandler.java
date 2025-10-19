package org.arsparadox.mobtalkerredux.vn.controller.vnmodules;

import org.arsparadox.mobtalkerredux.vn.controller.VisualNovelEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.arsparadox.mobtalkerredux.vn.controller.vnmodules.SaveHandler.processFinishing;
import static org.arsparadox.mobtalkerredux.vn.controller.vnmodules.StateHandler.findLabelId;
import static org.arsparadox.mobtalkerredux.vn.controller.vnmodules.VariableHandler.createVariable;

public class DialogueHandler {
    public static void processConditional(Map<String, Object> condition, VisualNovelEngine vn) {
        String conditionType = (String) condition.get("condition");
        boolean result = false;

        Object var = vn.localVariables.get(condition.get("var"));
        Object value = condition.get("value");
        long end = (long) condition.get("end");

        switch (conditionType) {
            case "equal":
                result = (var != null && value != null) &&
                        ((var instanceof Number && value instanceof Number)
                                ? ((Number) var).doubleValue() == ((Number) value).doubleValue()
                                : var.equals(value));
                break;
            case "not_equal":
                result = (var == null || value == null) ||
                        ((var instanceof Number && value instanceof Number)
                                ? ((Number) var).doubleValue() != ((Number) value).doubleValue()
                                : !var.equals(value));
                break;
            case "less_than":
                result = (var instanceof Number && value instanceof Number) && ((Number) var).doubleValue() < ((Number) value).doubleValue();
                break;
            case "greater_than":
                result = (var instanceof Number && value instanceof Number) && ((Number) var).doubleValue() > ((Number) value).doubleValue();
                break;
            case "night":
                result = !vn.isDay.get();
                break;
            case "day":
                result = vn.isDay.get();
                break;
        }

        vn.currentState.set(result ? vn.currentState.incrementAndGet() : end);
    }

    public static void processGlobalConditional(Map<String, Object> condition, VisualNovelEngine vn) {
        String conditionType = (String) condition.get("condition");
        boolean result = false;
        System.out.println("Condition = "+condition);
        System.out.println("Global Variable =" + vn.globalVariables);

        Object var = vn.globalVariables.get(condition.get("var"));
        Object value = condition.get("value");
        System.out.println("Value = "+ value);
        long end = (long) condition.get("end");
        System.out.println("End = "+end);

        switch (conditionType) {
            case "equal":
                System.out.println("var = "+var);
                System.out.println("value = "+value);
                result = (var != null && value != null) &&
                        ((var instanceof Number && value instanceof Number)
                                ? ((Number) var).doubleValue() == ((Number) value).doubleValue()
                                : var.equals(value));

                break;
            case "not_equal":
                result = (var == null || value == null) ||
                        ((var instanceof Number && value instanceof Number)
                                ? ((Number) var).doubleValue() != ((Number) value).doubleValue()
                                : !var.equals(value));
                break;
            case "less_than":
                result = (var instanceof Number && value instanceof Number) && ((Number) var).doubleValue() < ((Number) value).doubleValue();
                break;
            case "greater_than":
                result = (var instanceof Number && value instanceof Number) && ((Number) var).doubleValue() > ((Number) value).doubleValue();
                break;
            case "night":
                result = !vn.isDay.get();
                break;
            case "day":
                result = vn.isDay.get();
                break;
        }

        vn.currentState.set(result ? vn.currentState.incrementAndGet() : end);
    }

    public static void processJump(Map<String, Object> action, VisualNovelEngine vn) {
        vn.currentState.set(findLabelId((String) action.get("label"),vn.gameData));
        vn.currentState.incrementAndGet(); //TO-DO: Figure out if this is necessary (Update: Yes It Is)
    }

    public static void processMeta(Map<String, Object> action,VisualNovelEngine vn) {
        String actionType = (String) action.get("action");
        if ("create_var".equals(actionType)) {
            createVariable((String) action.get("var"), action.get("init"),vn.localVariables,vn.currentState);
        }
        else if("create_global".equals(actionType)){
            //Check First If Exist
            createVariable((String) action.get("var"), action.get("init"),vn.globalVariables,vn.currentState);
        }else {
            vn.currentState.incrementAndGet();
        }
    }

    public static void processNext(Map<String, Object> action,VisualNovelEngine vn) {
        vn.localVariables.put("checkpoint",action.get("label"));
    }

    public static void processIdleChat(
            VisualNovelEngine vn
    ){
        // Alright, Null Handling Time
        // Fuck...
        List<String> chats = (List<String>) vn.localVariables.getOrDefault("unlocked_events", new ArrayList<>());
        if (!chats.isEmpty()) {

            Random random = new Random();
            String chat = chats.get(random.nextInt(chats.size()));
            System.out.println(chat);
            vn.currentState.set(findLabelId(chat,vn.gameData));
            vn.currentState.incrementAndGet();
        } else {
            processFinishing(vn);
        }

    }


}
