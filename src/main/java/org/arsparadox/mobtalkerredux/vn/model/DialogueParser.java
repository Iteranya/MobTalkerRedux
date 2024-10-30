package org.arsparadox.mobtalkerredux.vn.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.arsparadox.mobtalkerredux.vn.data.dialogue.ActionsItem;
import org.arsparadox.mobtalkerredux.vn.data.dialogue.ChoiceItem;
import org.arsparadox.mobtalkerredux.vn.data.dialogue.Dialogue;
import org.arsparadox.mobtalkerredux.vn.data.dialogue.DialogueList;

import java.util.ArrayList;
import java.util.List;

public class DialogueParser {
    public static DialogueList parseDialogue(String jsonArray) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonArray);
            List<Dialogue> dialogueList = new ArrayList<>();

            for (JsonNode node : rootNode) {
                String type = node.get("type").asText();
                String action = node.get("action").asText();
                int id = node.get("id").asInt();

                // Initialize optional fields with null or default values
                String label = node.has("label") ? node.get("label").asText() : null;
                String content = node.has("content") ? node.get("content").asText() : null;
                String sprite = node.has("sprite") ? node.get("sprite").asText() : null;
                Integer amount = node.has("amount") ? node.get("amount").asInt() : null;
                String itemId = node.has("itemId") ? node.get("itemId").asText() : null;
                String condition = node.has("condition") ? node.get("condition").asText() : null;
                String var = node.has("var") ? node.get("var").asText() : null;
                Integer end = node.has("end") ? node.get("end").asInt() : null;
                Integer value = node.has("value") ? node.get("value").asInt() : null;
                Integer init = node.has("init") ? node.get("init").asInt() : null;

                // Handle choice array if present
                List<ChoiceItem> choices = new ArrayList<>();
                if (node.has("choice")) {
                    for (JsonNode choiceNode : node.get("choice")) {
                        choices.add(new ChoiceItem(
                                choiceNode.get("display").asText(),
                                choiceNode.get("label").asText()
                        ));
                    }
                }

                // Handle actions array if present
                List<ActionsItem> actions = new ArrayList<>();
                if (node.has("actions")) {
                    for (JsonNode actionNode : node.get("actions")) {
                        actions.add(new ActionsItem(
                                actionNode.get("action").asText(),
                                actionNode.has("label") ? actionNode.get("label").asText() : null,
                                actionNode.has("id") ? actionNode.get("id").asInt() : 0,
                                actionNode.has("type") ? actionNode.get("type").asText() : null,
                                actionNode.has("sprite") ? actionNode.get("sprite").asText() : null,
                                actionNode.has("content") ? actionNode.get("content").asText() : null
                        ));
                    }
                }

                // Create Dialogue object with all fields
                Dialogue dialogue = new Dialogue(
                        action,
                        id,
                        type,
                        label,
                        content,
                        sprite,
                        amount != null ? amount : 0,
                        itemId,
                        condition,
                        var,
                        end != null ? end : 0,
                        value != null ? value : 0,
                        actions,
                        choices,
                        init != null ? init : 0
                );

                dialogueList.add(dialogue);
            }

            return new DialogueList(dialogueList);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse dialogue JSON: " + e.getMessage(), e);
        }
    }
}
