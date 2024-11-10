package org.arsparadox.mobtalkerredux.vn.controller.vnmodules;

import org.arsparadox.mobtalkerredux.vn.data.DialogueState;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class StateHandler {

    public static Long findLabelId(String var, List<Map<String, Object>> gameData) {
        System.out.println("Trying to find: "+var);
        return gameData.stream()
                .filter(action -> "label".equals(action.get("type")) && var.equals(action.get("label")))
                .map(action -> (long) action.get("id"))
                .findFirst()
                .orElse(null);
    }

    public static Map<String, Object> getDictById(long targetId, List<Map<String, Object>> gameData) {
        return gameData.stream()
                .filter(action -> targetId == (long) action.get("id"))
                .findFirst()
                .orElse(null);
    }

    public static void updateDialogue(
            String label, String content, DialogueState state, AtomicBoolean isEngineRunning, AtomicLong currentState
    ) {
        state.setLabel(label);
        state.setContent(content);
        isEngineRunning.set(false);
        currentState.incrementAndGet();
    }

    public static void updateBackground(String background,DialogueState state, AtomicLong currentState) {
        state.setBackground(background);

        currentState.incrementAndGet();
    }

    public static void updateChoices(List<Map<String, Object>> choices, DialogueState state, AtomicBoolean isEngineRunning) {
        state.setChoices(choices);
        isEngineRunning.set(false);
    }

    public static void updateCommand(Map<String, Object> value,DialogueState state) {
        String action = (String) value.get("action");
        state.setCommand(action);
    }


}
