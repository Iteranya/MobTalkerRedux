package org.arsparadox.mobtalkerredux.vn.controller;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.arsparadox.mobtalkerredux.vn.view.DialogueScreen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisualNovelEngine {
    private List<Map<String, Object>> gameData;
    private int minId;
    private int maxId;
    private int currentState;
    private Map<String, Object> variables;

    public boolean isEngineRunning = false;

    public VisualNovelEngine(List<Map<String, Object>> gameData) {
        this.gameData = gameData;
//        this.minId = gameData.stream()
//                .map(map -> (int) map.getOrDefault("id", Integer.MAX_VALUE))
//                .min(Comparator.naturalOrder())
//                .orElse(0);
//        this.maxId = gameData.stream()
//                .map(map -> (int) map.getOrDefault("id", Integer.MIN_VALUE))
//                .max(Comparator.naturalOrder())
//                .orElse(0);
        this.currentState = 0;
        this.variables = new HashMap<>();

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

    private void showSprite(String spritePath, DialogueScreen dialogueScreen, PoseStack poseStack) {
        ResourceLocation location = new ResourceLocation(
                "textures/characters/"+spritePath
        );
        dialogueScreen.renderCharacterSprite(poseStack,location);
        this.currentState++;
    }

    private void showDialogue(String label, String content, DialogueScreen dialogueScreen, PoseStack poseStack) {
        dialogueScreen.renderDialogueBox(poseStack,content);
        dialogueScreen.renderCharacterName(poseStack,label);
        this.isEngineRunning = false;
        this.currentState++;
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
        currentState = findLabelId((String) action.get("label"));
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
    private void showChoices(List<Map<String, Object>> choices, DialogueScreen dialogueScreen) {
        dialogueScreen.renderChoiceButtons(choices);
        this.isEngineRunning = false;
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
    private boolean processAction(Map<String, Object> action, DialogueScreen dialogueScreen, PoseStack poseStack) {
        String actionType = (String) action.get("type");

        switch (actionType) {
            case "show_sprite":
                showSprite((String) action.get("sprite"),dialogueScreen,poseStack);
                return true;
            case "dialogue":
                showDialogue((String) action.get("label"), (String) action.get("content"),dialogueScreen,poseStack);
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
                showChoices((List<Map<String, Object>>) action.get("choice"),dialogueScreen);
                break;
            case "command":
                processCommand(action);
                break;
            case "label":
                this.currentState++;
                break;
            case "finish_dialogue":
                return true;
            default:
                this.currentState++;
                break;
        }
        return false;
    }

    public void readScript(DialogueScreen dialogueScreen, PoseStack poseStack) {
        while (true) { // Infinite loop
            if (isEngineRunning) { // Check if engine is running
                Map<String, Object> action = getDictById(currentState);
                if ("meta".equals(action.get("type"))) {
                    processMeta(action);
                } else {
                    processAction(action, dialogueScreen, poseStack);
                }
            } else {
                try {
                    Thread.sleep(100); // Pause briefly to avoid busy-waiting
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupt status
                    break; // Exit loop if the thread is interrupted
                }
            }
        }
    }

    public void startReadingScript(DialogueScreen dialogueScreen, PoseStack poseStack) {
        // Schedule this to run every game tick
        MinecraftForge.EVENT_BUS.register(new Object() {
            @SubscribeEvent
            public void onClientTick(TickEvent.ClientTickEvent event) {
                if (event.phase == TickEvent.Phase.END) { // Only act at the end of each tick
                    if (isEngineRunning) {
                        readScriptStep(dialogueScreen, poseStack);
                    } else {
                        MinecraftForge.EVENT_BUS.unregister(this); // Unregister to stop the task
                    }
                }
            }
        });
    }

    private void readScriptStep(DialogueScreen dialogueScreen, PoseStack poseStack) {
        Map<String, Object> action = getDictById(currentState);
        if ("meta".equals(action.get("type"))) {
            processMeta(action);
        } else {
            processAction(action, dialogueScreen, poseStack);
        }
    }


    public void changeStateByLabel(String label) {
        this.currentState = findLabelId(label);

    }
}
