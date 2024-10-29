package org.arsparadox.mobtalkerredux.vn.controller;

import org.arsparadox.mobtalkerredux.vn.data.dialogue.Action;
import org.arsparadox.mobtalkerredux.vn.data.dialogue.State;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record VisualNovelFSM(
        String initialState,
        Map<String, Object> variables,
        Map<String, State> states
) {
    // Helper method to parse JSON into actions
    private static Action parseAction(JsonObject actionObj) {
        return switch (actionObj.getString("type")) {
            case "show_sprite" -> new Action.ShowSpriteAction(
                    actionObj.getString("sprite")
            );
            case "dialogue" -> new Action.DialogueAction(
                    actionObj.getString("label"),
                    actionObj.getString("content")
            );
            case "modify_variable" -> new Action.ModifyVariableAction(
                    actionObj.getString("variable"),
                    actionObj.getString("operation"),
                    actionObj.get("value")
            );
            case "transition" -> new Action.TransitionAction(
                    actionObj.getString("action"),
                    actionObj.getString("label")
            );
            case "give_item" -> new Action.GiveItemAction(
                    actionObj.getString("item"),
                    actionObj.getInt("amount")
            );
            case "finish_dialogue" -> new Action.FinishDialogueAction();
            case "conditional" -> {
                List<Action> conditionalActions = new ArrayList<>();
                JsonArray actionsArray = actionObj.getJsonArray("actions");
                for (int i = 0; i < actionsArray.size(); i++) {
                    conditionalActions.add(parseAction(actionsArray.getJsonObject(i)));
                }
                yield new Action.ConditionalAction(
                        actionObj.getString("condition"),
                        actionObj.getString("variable"),
                        actionObj.get("value"),
                        conditionalActions
                );
            }
            default -> throw new IllegalArgumentException("Unknown action type");
        };
    }

    private static List<Action> parseActions(JsonArray actions) {
        List<Action> result = new ArrayList<>();
        for (JsonValue action : actions) {
            result.add(parseAction((JsonObject) action));
        }
        return result;
    }

    public static VisualNovelFSM fromJson(JsonObject json) {
        String initialState = json.getString("initial_state");
        Map<String, Object> variables = new HashMap<>();
        JsonObject vars = json.getJsonObject("variables");
        for (String key : vars.keySet()) {
            variables.put(key, vars.get(key));
        }

        Map<String, State> states = new HashMap<>();
        JsonObject statesObj = json.getJsonObject("states");
        for (String stateName : statesObj.keySet()) {
            JsonObject stateObj = statesObj.getJsonObject(stateName);

            List<Action> actions = parseActions(stateObj.getJsonArray("actions"));

            Map<String, String> choices = new HashMap<>();
            if (stateObj.containsKey("choices")) {
                JsonObject choicesObj = stateObj.getJsonObject("choices");
                for (String choice : choicesObj.keySet()) {
                    choices.put(choice, choicesObj.getString(choice));
                }
            }

            states.put(stateName, new State(actions, choices));
        }

        return new VisualNovelFSM(initialState, variables, states);
    }
}