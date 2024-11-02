package org.arsparadox.mobtalkerredux.vn.controller;

import net.minecraft.resources.ResourceLocation;
import org.arsparadox.mobtalkerredux.vn.data.DialogueState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisualNovelEngine {
    public boolean shutdown = false;
    private List<Map<String, Object>> gameData;
    private int currentState=0;
    private Map<String, Object> variables;

    public DialogueState state;
    public boolean isEngineRunning = false;

    public VisualNovelEngine(List<Map<String, Object>> gameData) {
        this.gameData = gameData;
        this.currentState = 0;
        this.variables = new HashMap<>();
        this.state = new DialogueState(null,null,null,null);
    }

    private Integer findLabelId(String var) {
        return gameData.stream()
                .filter(action -> "label".equals(action.get("type")) && var.equals(action.get("label")))
                .map(action -> (int) action.get("id"))
                .findFirst()
                .orElse(null);
    }

    private Map<String, Object> getDictById(int targetId) {
        return gameData.stream()
                .filter(action -> targetId == (int) action.get("id"))
                .findFirst()
                .orElse(null);
    }

    private void updateSprite(String spritePath) {
        ResourceLocation location = new ResourceLocation(
                "mobtalkerredux", "textures/" + spritePath
        );
        state.setSprite(location);
        this.currentState++;
    }

    private void updateDialogue(String label, String content) {
        state.setLabel(label);
        state.setContent(content);
        this.isEngineRunning = false;
        this.currentState++;
    }

    private void updateChoices(List<Map<String, Object>> choices) {
        state.setChoices(choices);
        this.isEngineRunning = false;
    }

    private Object processCommand(Map<String, Object> value) {
        String action = (String) value.get("action");
        if ("get_gamemode".equals(action)) {
            return "Survival";
        } else if ("custom_command".equals(action)) {
            return "Nothing for now";
        }
        return "Nothing for now";
    }

    @SuppressWarnings("unchecked")
    private void modifyVariable(String variable, String operation, Object value) {
        if (value instanceof Map) {
            value = processCommand((Map<String, Object>) value);
        }

        if (operation.equals("increment_var")) {
            if (this.variables.get(variable) instanceof Number && value instanceof Number) {
                double result = ((Number) this.variables.get(variable)).doubleValue() +
                        ((Number) value).doubleValue();
                this.variables.put(variable, result);
            }
        } else if (operation.equals("substract_var")) {
            if (this.variables.get(variable) instanceof Number && value instanceof Number) {
                double result = ((Number) this.variables.get(variable)).doubleValue() -
                        ((Number) value).doubleValue();
                this.variables.put(variable, result);
            }
        } else {
            this.variables.put(variable, value);
        }
        this.currentState++;
    }

    private void giveItem(String item, int amount) {

        this.currentState++;
    }

    private void processJump(Map<String, Object> action) {
        this.currentState = findLabelId((String) action.get("label"));
    }

    @SuppressWarnings("unchecked")
    private void processConditional(Map<String, Object> condition) {
        Object var = this.variables.get(condition.get("var"));
        Object value = condition.get("value");
        int end = (int) condition.get("end");

        if (value instanceof Map) {
            value = processCommand((Map<String, Object>) value);
        }

        String conditionType = (String) condition.get("condition");
        boolean result = false;

        switch (conditionType) {
            case "equal":
                result = var.equals(value);
                break;
            case "not_equal":
                result = !var.equals(value);
                break;
            case "less_than":
                if (var instanceof Number && value instanceof Number) {
                    result = ((Number) var).doubleValue() < ((Number) value).doubleValue();
                }
                break;
            case "greater_than":
                if (var instanceof Number && value instanceof Number) {
                    result = ((Number) var).doubleValue() > ((Number) value).doubleValue();
                }
                break;
        }

        this.currentState = result ? this.currentState + 1 : end;
    }

    @SuppressWarnings("unchecked")


    private void createVariable(String varName, Object varInit) {
        this.variables.put(varName, varInit);
        this.currentState++;
    }

    private void processMeta(Map<String, Object> action) {
        String actionType = (String) action.get("action");
        if ("create_var".equals(actionType)) {
            createVariable((String) action.get("var"), action.get("init"));
        } else {
            this.currentState++;
        }
    }

    @SuppressWarnings("unchecked")
    private boolean processAction(Map<String, Object> action) {
        String actionType = (String) action.get("type");

        switch (actionType) {
            case "show_sprite":
                updateSprite((String) action.get("location"));
                return true;
            case "dialogue":
                updateDialogue((String) action.get("label"), (String) action.get("content"));
                return true;
            case "modify_variable":
                modifyVariable((String) action.get("var"),
                        (String) action.get("action"),
                        action.get("value"));
                break;
            case "give_item":
                giveItem((String) action.get("item"), (int) action.get("amount"));
                break;
            case "conditional":
                processConditional(action);
                break;
            case "transition":
                if ("jump".equals(action.get("action"))) {
                    processJump(action);
                }
                break;
            case "choice":
                System.out.println("Try to yoink choice"+ action);
                updateChoices((List<Map<String, Object>>) action.get("choice"));
                break;
            case "command":
                processCommand(action);
                break;
            case "label":
                this.currentState++;
                break;
            case "finish_dialogue":
                isEngineRunning=false;
                shutdown = true;
            default:
                this.currentState++;
                break;
        }
        return false;
    }

    public void runEngine() {
        while (isEngineRunning) { // Infinite loop
            // Check if engine is running
            Map<String, Object> action = getDictById(this.currentState);
            if(action == null){
                shutdown = true;
                isEngineRunning = false;
                return;
            }
            if ("meta".equals(((Map<?, ?>) action).get("type"))) {
                 processMeta(action);
            } else {
                processAction(action);
            }

        }
    }


    public int changeStateByLabel(String label) {
        this.currentState = findLabelId(label);
        return this.currentState;

    }

    public DialogueState getNext() {
        return this.state;
    }

    public void buttonPress(String choice) {
        changeStateByLabel(choice);
        this.state.setChoices(new ArrayList<>());
    }
}
