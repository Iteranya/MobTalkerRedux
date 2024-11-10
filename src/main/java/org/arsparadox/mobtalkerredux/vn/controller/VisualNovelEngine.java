package org.arsparadox.mobtalkerredux.vn.controller;

import org.arsparadox.mobtalkerredux.vn.controller.vnmodules.PlayerInventoryHandler;
import org.arsparadox.mobtalkerredux.vn.data.DialogueState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.arsparadox.mobtalkerredux.vn.controller.vnmodules.DialogueHandler.*;
import static org.arsparadox.mobtalkerredux.vn.controller.vnmodules.SpriteHandler.removeSprite;
import static org.arsparadox.mobtalkerredux.vn.controller.vnmodules.SpriteHandler.updateSprite;
import static org.arsparadox.mobtalkerredux.vn.controller.vnmodules.StateHandler.*;
import static org.arsparadox.mobtalkerredux.vn.controller.vnmodules.VariableHandler.initializeVariable;
import static org.arsparadox.mobtalkerredux.vn.controller.vnmodules.VariableHandler.modifyVariable;


// I should really refactor this before I regret  everything...
public class VisualNovelEngine {

    public AtomicBoolean shutdown = new AtomicBoolean(false);
    private List<Map<String, Object>> gameData;
    private AtomicLong currentState = new AtomicLong(0);
    private Map<String, Object> variables = new HashMap<>();

    public DialogueState state;
    public AtomicBoolean isEngineRunning = new AtomicBoolean(false);

    public StringBuffer scriptName = new StringBuffer();

    public StringBuffer uid = new StringBuffer();

    public AtomicBoolean isDay = new AtomicBoolean(true);

    public PlayerInventoryHandler inventoryHandler;


    public VisualNovelEngine(List<Map<String, Object>> gameData,String scriptName, String uid, boolean day,PlayerInventoryHandler inventory) {
        this.uid.setLength(0);
        this.uid.append(uid);
        this.gameData = gameData;
        this.state = new DialogueState(null,null,null);
        this.scriptName.setLength(0);
        this.scriptName.append(scriptName);
        this.isDay.set(day);
        this.inventoryHandler = inventory;
        this.variables.put("type","variable");
        initializeVariable(this.gameData,this.variables,this.currentState);
    }

    // Look, for the sake of my own sanity, I have to refactor this thing...

    @SuppressWarnings("unchecked")
    private boolean processAction(Map<String, Object> action) {
        String actionType = (String) action.get("type");

        switch (actionType) {
            case "show_sprite":
                updateSprite(action, state, currentState);
                return true;
            case "remove_sprite":
                removeSprite((String) action.get("sprite"), state);
            case "dialogue":
                updateDialogue(
                        (String) action.get("label"),
                        (String) action.get("content"),
                        state, isEngineRunning, currentState);
                return true;
            case "modify_variable":
                modifyVariable((String) action.get("var"),
                        (String) action.get("action"),
                        action.get("value"),
                        variables, currentState);
                break;
            case "give_item":
                inventoryHandler.giveItemToPlayer((String) action.get("item"), (int) (long) action.get("amount"));
                break;
            case "conditional":
                processConditional(action, isDay, variables, currentState);
                break;
            case "transition":
                if ("jump".equals(action.get("action"))) {
                    processJump(action, currentState, gameData);
                }
                break;
            case "choice":
                System.out.println("Try to yoink choice" + action);
                updateChoices(
                        (List<Map<String, Object>>) action.get("choice"),
                        state, isEngineRunning
                );
                break;
            case "command":
                updateCommand(action, state);
                this.currentState.incrementAndGet();
                break;
            case "label":
                this.currentState.incrementAndGet();
                break;
            case "modify_background":
                updateBackground((String) action.get("background"), state, currentState);
                break;
            case "clear_background":
                state.clearBackground();
                this.currentState.incrementAndGet();
                break;
            case "night_choice":
                if (!isDay.get()) {
                    updateChoices((List<Map<String, Object>>) action.get("choice"), state, isEngineRunning);
                } else {
                    this.currentState.incrementAndGet();
                }
                break;
            case "unlock_dialogues":
                List<String> events = (List<String>) this.variables.getOrDefault("unlocked_events", new ArrayList<>());
                events.addAll((List<String>) action.get("events"));
                this.variables.put("unlocked_events", events);
                this.currentState.incrementAndGet();
                break;
            case "next":
                processNext(action,variables);
                this.currentState.incrementAndGet();
                break;
            case "idle_chat":
                processIdleChat(variables,currentState,gameData,isEngineRunning,scriptName,uid,shutdown);
                break;
            case "finish_dialogue":
                processFinishing(variables,isEngineRunning,gameData,scriptName,uid,shutdown);
            case "check_inventory":
            default:
                this.currentState.incrementAndGet();
                break;
        }
        return false;
    }



    public void runEngine() {
        while (isEngineRunning.get()) { // Infinite loop
            // Check if engine is running
            System.out.println(this.currentState);
            Map<String, Object> action = getDictById(this.currentState.get(),gameData);
            if(action == null){
                shutdown.set(true);
                isEngineRunning.set(false);
                return;
            }
            if ("meta".equals(((Map<?, ?>) action).get("type"))) {
                 processMeta(action,variables,currentState);
            } else {
                processAction(action);
            }

        }
    }

    public DialogueState getNext() {
        return this.state;
    }

    public void buttonPress(String choice) {
        changeStateByLabel(choice,currentState,gameData);
        this.state.setChoices(new ArrayList<>());
    }
}
