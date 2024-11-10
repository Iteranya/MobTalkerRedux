package org.arsparadox.mobtalkerredux.vn.controller.vnmodules;

import org.arsparadox.mobtalkerredux.vn.model.ScriptLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.arsparadox.mobtalkerredux.vn.controller.vnmodules.StateHandler.findLabelId;
import static org.arsparadox.mobtalkerredux.vn.controller.vnmodules.VariableHandler.createVariable;

public class DialogueHandler {
    @SuppressWarnings("unchecked")
    public static void processConditional(Map<String, Object> condition, AtomicBoolean isDay, Map<String, Object> variables, AtomicLong currentState) {
        String conditionType = (String) condition.get("condition");
        boolean result = false;

        Object var = variables.get(condition.get("var"));
        Object value = condition.get("value");
        long end = (long) condition.get("end");

        System.out.println("Is it day time???: "+isDay);

        switch (conditionType) {
            case "equal":
                result = (var != null) && var.equals(value);
                break;
            case "not_equal":
                result = (var == null) || !var.equals(value);
                break;
            case "less_than":
                result = (var instanceof Number && value instanceof Number) && ((Number) var).doubleValue() < ((Number) value).doubleValue();
                break;
            case "greater_than":
                result = (var instanceof Number && value instanceof Number) && ((Number) var).doubleValue() > ((Number) value).doubleValue();
                break;
            case "night":
                result = !isDay.get();
                break;
            case "day":
                result = isDay.get();
                break;
        }

        currentState.set(result ? currentState.incrementAndGet() : end);
    }

    public static void processJump(Map<String, Object> action, AtomicLong currentState, List<Map<String, Object>> gameData) {
        currentState.set(findLabelId((String) action.get("label"),gameData));
        currentState.incrementAndGet(); //TO-DO: Figure out if this is necessary (Update: Yes It Is)
    }

    public static void processMeta(Map<String, Object> action,Map<String, Object> variables,AtomicLong currentState) {
        String actionType = (String) action.get("action");
        if ("create_var".equals(actionType)) {
            createVariable((String) action.get("var"), action.get("init"),variables,currentState);
        } else {
            currentState.incrementAndGet();
        }
    }

    public static void processNext(Map<String, Object> action,Map<String, Object> variables) {
        variables.put("checkpoint",action.get("label"));
    }

    public static void processIdleChat(
            Map<String, Object> variables,AtomicLong currentState,
            List<Map<String, Object>> gameData, AtomicBoolean isEngineRunning,
            StringBuffer scriptName,StringBuffer uid,AtomicBoolean shutdown
    ){
        // Alright, Null Handling Time
        // Fuck...
        System.out.println(variables.get("unlocked_events"));
        List<String> chats = (List<String>) variables.getOrDefault("unlocked_events", new ArrayList<>());
        if (!chats.isEmpty()) {

            Random random = new Random();
            String chat = chats.get(random.nextInt(chats.size()));
            System.out.println(chat);
            currentState.set(findLabelId(chat,gameData));
            currentState.incrementAndGet();
        } else {
            processFinishing(variables,isEngineRunning,gameData,scriptName,uid,shutdown);
        }

    }

    public static void processFinishing(
            Map<String, Object> variables,AtomicBoolean isEngineRunning,
            List<Map<String, Object>> gameData,StringBuffer scriptName,
            StringBuffer uid, AtomicBoolean shutdown
    ) {
        isEngineRunning.set(false);
        ScriptLoader.saveState(gameData,scriptName.toString(),uid.toString());
        variables.put("type", "variable");
        shutdown.set(true);
    }
}
