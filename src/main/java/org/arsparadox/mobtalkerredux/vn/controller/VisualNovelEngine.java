package org.arsparadox.mobtalkerredux.vn.controller;

import org.arsparadox.mobtalkerredux.vn.data.DialogueState;
import org.arsparadox.mobtalkerredux.vn.data.SpriteState;
import org.arsparadox.mobtalkerredux.vn.model.ScriptLoader;

import java.util.*;

public class VisualNovelEngine {
    public boolean shutdown = false;
    private List<Map<String, Object>> gameData;
    private long currentState=0;
    private Map<String, Object> variables;

    public DialogueState state;
    public boolean isEngineRunning = false;

    public String scriptName;

    public VisualNovelEngine(List<Map<String, Object>> gameData,String scriptName) {
        this.gameData = gameData;
        this.currentState = 0;
        this.state = new DialogueState(null,null,null);
        this.scriptName = scriptName;
        initializeVariable();
    }

    private void initializeVariable() {
        if(this.gameData.get(0).get("type")!="variable"){
            this.variables = new HashMap<>();
            this.variables.put("type", "variable");
            this.gameData.add(0, variables);
        }else{
            this.variables = this.gameData.get(0);
        }
    }

    private Long findLabelId(String var) {
        return gameData.stream()
                .filter(action -> "label".equals(action.get("type")) && var.equals(action.get("label")))
                .map(action -> (long) action.get("id"))
                .findFirst()
                .orElse(null);
    }

    private Map<String, Object> getDictById(long targetId) {
        return gameData.stream()
                .filter(action -> targetId == (long) action.get("id"))
                .findFirst()
                .orElse(null);
    }

    private void removeSprite(String remove){
        //System.out.println("Try to remove sprite: "+ remove);
        removeSpriteByFolder(this.state.getSprites(), remove);
    }

    private void updateSprite(Map<String, Object> sprite) {
        String spritePos;
        if(sprite.get("position")==null){
            spritePos = "CUSTOM";
        }
        else{
            spritePos = (String) sprite.get("position");
        }
        SpriteState newSprite;
        newSprite = (new SpriteState(
                (String) sprite.get("sprite"),
                (String) sprite.get("location"),
                spritePos
        ));

        if(Objects.equals((String) sprite.get("action"), "show")){
            if(sprite.get("wRatio")!=null){
                newSprite.setPositioning(
                        ((Long) sprite.get("wRatio")).intValue(),
                        ((Long) sprite.get("hRatio")).intValue(),
                        ((Long) sprite.get("wFrameRatio")).intValue(),
                        ((Long) sprite.get("hFrameRatio")).intValue(),
                        ((Long) sprite.get("column")).intValue(),
                        ((Long) sprite.get("row")).intValue()
                );
            }
            for (SpriteState oldSprite: this.state.getSprites()) {

                if(Objects.equals(oldSprite.getSprite(), newSprite.getSprite())){
                    removeSpriteByFolder(this.state.getSprites(), newSprite.getSprite());
                    break;
                }
            }
            //System.out.println("Adding New Sprite: " + newSprite.getSprite());
            this.state.addSprite(newSprite);
        }
        this.currentState++;
    }
    public void removeSpriteByFolder(List<SpriteState> sprites, String folderName) {
        sprites.removeIf(sprite -> sprite.getSprite().equals(folderName));
    }
    private void updateDialogue(String label, String content) {
        state.setLabel(label);
        state.setContent(content);
        this.isEngineRunning = false;
        this.currentState++;
    }

    private void updateBackground(String background) {
        state.setBackground(background);

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
            // NOT IMPLEMENTED YET
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

    private void giveItem(String item, long amount) {
        // Not Implemented Yet
        this.currentState++;
    }

    private void processJump(Map<String, Object> action) {
        this.currentState = findLabelId((String) action.get("label"));
        this.currentState++; //TO-DO: Figure out if this is necessary (Update: Yes It Is)
    }

    @SuppressWarnings("unchecked")
    private void processConditional(Map<String, Object> condition) {
        Object var = this.variables.get(condition.get("var"));
        Object value = condition.get("value");
        long end = (long) condition.get("end");

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
                updateSprite(action);
                return true;
            case "remove_sprite":
                removeSprite((String) action.get("sprite"));
            case "dialogue":
                updateDialogue((String) action.get("label"), (String) action.get("content"));
                return true;
            case "modify_variable":
                modifyVariable((String) action.get("var"),
                        (String) action.get("action"),
                        action.get("value"));
                break;
            case "give_item":
                giveItem((String) action.get("item"), (long) action.get("amount"));
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
            case "modify_background":
                updateBackground((String) action.get("background"));
                break;
            case "clear_background":
                state.clearBackground();
                this.currentState++;
                break;
            case "finish_dialogue":
                processFinishing();
            default:
                this.currentState++;
                break;
        }
        return false;
    }

    private void processFinishing() {
        isEngineRunning=false;
        ScriptLoader.saveState(gameData,scriptName);
        shutdown = true;
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


    public long changeStateByLabel(String label) {
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
