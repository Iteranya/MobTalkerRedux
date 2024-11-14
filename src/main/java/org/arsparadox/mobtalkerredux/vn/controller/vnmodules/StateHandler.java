package org.arsparadox.mobtalkerredux.vn.controller.vnmodules;

import org.arsparadox.mobtalkerredux.vn.controller.VisualNovelEngine;
import org.arsparadox.mobtalkerredux.vn.data.DialogueState;

import java.util.List;
import java.util.Map;
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
            String label, String content, VisualNovelEngine vn
            ) {
        vn.state.setLabel(label);
        vn.state.setContent(content);
        vn.isEngineRunning.set(false);
        vn.currentState.incrementAndGet();
    }

    public static void updateBackground(String background,VisualNovelEngine vn) {
        vn.state.setBackground(background);

        vn.currentState.incrementAndGet();
    }

    public static void updateChoices(List<Map<String, Object>> choices, VisualNovelEngine vn) {
        vn.state.setChoices(choices);
        vn.isEngineRunning.set(false);
    }

    public static void updateCommand(Map<String, Object> value,DialogueState state) {
        String action = (String) value.get("action");
        state.setCommand(action);
    }

    public static void changeStateByLabel(String label,AtomicLong currentState, List<Map<String, Object>> gameData) {
        currentState.set(findLabelId(label,gameData));
    }

    public static void updateMusic(VisualNovelEngine vn,String music){
        vn.state.setMusic(music);
    }

    public static void stopMusic(VisualNovelEngine vn){
        vn.state.setMusic(null);
    }

    public static void updateSound(VisualNovelEngine vn, String sound){
        vn.state.setSound(sound);
    }
}
