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

    public String uid;

    public boolean isDay;


    public VisualNovelEngine(List<Map<String, Object>> gameData,String scriptName, String uid, boolean day) {
        this.uid = uid;
        this.gameData = gameData;
        this.currentState = 0;
        this.state = new DialogueState(null,null,null);
        this.scriptName = scriptName;
        this.isDay = day;
        initializeVariable();
    }

    private void initializeVariable() {
        if(!"variable".equals(this.gameData.get(this.gameData.size() - 1).get("type"))){
            System.out.println(this.gameData.get(this.gameData.size() - 1).get("type"));
            this.variables = new HashMap<>();
            this.variables.put("type","variable");
            this.gameData.add(variables);
            System.out.println("Initialize Variable");
        }else{
            this.variables = this.gameData.get(this.gameData.size() - 1);
            if(this.variables.get("checkpoint")!=null && !((String) this.variables.get("checkpoint")).isEmpty()){
                this.currentState = findLabelId((String) this.variables.get("checkpoint"));
            }

        }
    }

    private Long findLabelId(String var) {
        System.out.println("Trying to find: "+var);
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
        String conditionType = (String) condition.get("condition");
        boolean result = false;

        Object var = this.variables.get(condition.get("var"));
        Object value = condition.get("value");
        long end = (long) condition.get("end");

        if (value instanceof Map) {
            value = processCommand((Map<String, Object>) value);
        }

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
                result = !isDay;
                break;
            case "day":
                result = isDay;
                break;
        }

        this.currentState = result ? this.currentState + 1 : end;
    }


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
            case "night_choice":
                if(!isDay){
                    updateChoices((List<Map<String, Object>>) action.get("choice"));
                }else{
                    this.currentState++;
                }
                break;
            case "unlock_dialogues":
                List<String> events = (List<String>) this.variables.getOrDefault("unlocked_events", new ArrayList<>());
                events.addAll((List<String>) action.get("events"));
                this.variables.put("unlocked_events", events);
                this.currentState++;
                break;
            case "next":
                processNext(action);
                this.currentState++;
                break;
            case "idle_chat":
                processIdleChat();
                break;
            case "finish_dialogue":
                processFinishing();
            default:
                this.currentState++;
                break;
        }
        return false;
    }

    private void processNext(Map<String, Object> action) {
        this.variables.put("checkpoint",action.get("label"));
    }

    private void processIdleChat(){
        // Alright, Null Handling Time
        // Fuck...
        System.out.println(this.variables.get("unlocked_events"));
        List<String> chats = (List<String>) this.variables.getOrDefault("unlocked_events", new ArrayList<>());
        if (!chats.isEmpty()) {

            Random random = new Random();
            String chat = chats.get(random.nextInt(chats.size()));
            System.out.println(chat);
            this.currentState = findLabelId(chat);
            this.currentState++;
        } else {
            processFinishing();
        }

    }

    private void processFinishing() {
        isEngineRunning=false;
        ScriptLoader.saveState(gameData,scriptName,uid);
        this.variables.put("type", "variable");
        shutdown = true;
    }

    public void runEngine() {
        while (isEngineRunning) { // Infinite loop
            // Check if engine is running
            System.out.println(this.currentState);
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
