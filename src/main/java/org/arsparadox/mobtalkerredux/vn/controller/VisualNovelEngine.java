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
import static org.arsparadox.mobtalkerredux.vn.controller.vnmodules.SaveHandler.processFinishing;
import static org.arsparadox.mobtalkerredux.vn.controller.vnmodules.SpriteHandler.removeSprite;
import static org.arsparadox.mobtalkerredux.vn.controller.vnmodules.SpriteHandler.updateSprite;
import static org.arsparadox.mobtalkerredux.vn.controller.vnmodules.StateHandler.*;
import static org.arsparadox.mobtalkerredux.vn.controller.vnmodules.VariableHandler.initializeVariable;
import static org.arsparadox.mobtalkerredux.vn.controller.vnmodules.VariableHandler.modifyVariable;


// I should really refactor this before I regret  everything...
public class VisualNovelEngine {

    public AtomicBoolean shutdown = new AtomicBoolean(false);
    public List<Map<String, Object>> gameData;
    public List<Map<String, Object>> saves;
    public AtomicLong currentState = new AtomicLong(0);
    public Map<String, Object> variables = new HashMap<>();

    public DialogueState state;
    public AtomicBoolean isEngineRunning = new AtomicBoolean(false);

    public StringBuffer scriptName = new StringBuffer();

    public StringBuffer uid = new StringBuffer();

    public AtomicBoolean isDay = new AtomicBoolean(true);

    public PlayerInventoryHandler inventoryHandler;


    public VisualNovelEngine(List<Map<String, Object>> gameData,String scriptName, String uid, boolean day,PlayerInventoryHandler inventory,List<Map<String, Object>> save) {
        this.uid.setLength(0);
        this.uid.append(uid);
        this.gameData = gameData;
        this.saves = save;
        this.state = new DialogueState(null,null,null);
        this.scriptName.setLength(0);
        this.scriptName.append(scriptName);
        this.isDay.set(day);
        this.inventoryHandler = inventory;
        this.variables.put("type","variable");
        initializeVariable(this);
    }

    // Look, for the sake of my own sanity, I have to refactor this thing...

    @SuppressWarnings("unchecked")
    private void processAction(Map<String, Object> action) {
        String actionType = (String) action.get("type");

        switch (actionType) {
            case "show_sprite":
                updateSprite(action, this);
                return;
            case "remove_sprite":
                removeSprite((String) action.get("sprite"), this);
                return;
            case "dialogue":
                String sound = (String) action.get("voice");
                updateDialogue(
                        (String) action.get("label"),
                        (String) action.get("content"),
                        (String) action.get("voice"),
                        this);
                return;
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
                processConditional(action, this);
                break;
            case "transition":
                if ("jump".equals(action.get("action"))) {
                    processJump(action,this);
                }
                break;
            case "choice":
                System.out.println("Try to yoink choice" + action);
                updateChoices(
                        (List<Map<String, Object>>) action.get("choice"), this
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
                updateBackground((String) action.get("background"),this);
                break;
            case "clear_background":
                state.clearBackground();
                this.currentState.incrementAndGet();
                break;
            case "night_choice":
                if (!isDay.get()) {
                    updateChoices((List<Map<String, Object>>) action.get("choice"), this);
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
            case "play_sound":
                updateSound(this, (String) action.get("sound"));
            case "play_music":
                if(action.get("music")!=null){
                    updateMusic(this, (String) action.get("music"));
                }else{
                    stopMusic(this);
                }

            case "next":
                processNext(action,this);
                this.currentState.incrementAndGet();
                break;
            case "idle_chat":
                processIdleChat(this);
                break;
            case "finish_dialogue":
                processFinishing(this);
            case "check_inventory":
            default:
                this.currentState.incrementAndGet();
                break;
        }
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
                 processMeta(action,this);
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
