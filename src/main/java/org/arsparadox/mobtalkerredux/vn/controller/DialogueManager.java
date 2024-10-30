package org.arsparadox.mobtalkerredux.vn.controller;

import com.mojang.blaze3d.vertex.PoseStack;
import org.arsparadox.mobtalkerredux.vn.data.dialogue.*;

import java.util.ArrayList;
import java.util.List;

public class DialogueManager {
    private final DialogueUpdater updater;
    private List<ChoiceItem> currentChoices = new ArrayList<>();
    private List<Variable> variables = new ArrayList<>();

//    private DialogueBoxComponent dialogueBoxComponent;
//    private ImageComponent imageComponent;
//    private ForegroundComponent foregroundComponent;
//    private BackgroundComponent backgroundComponent;
    private final List<Dialogue> dialogueList;

    private int currentId;

    // This is the part where I replicate the FSM Loader Machine in Python into Java

    // Ugh... my head...

    public DialogueManager(DialogueList dialogueList, DialogueUpdater updater) {
        this.dialogueList = dialogueList.dialogueList();
        this.updater = updater;
        // Start with the first dialogue entry if available
        if (!dialogueList.dialogueList().isEmpty()) {
            initializeDialogue();
        }
    }

    public void updateDialogue(){

    }

    public void update(PoseStack poseStack) {
        // Update the display with current state
        Dialogue dialogue = findDialogueById(this.currentId);

    }

    public Dialogue findDialogueById(int id){
        for (Dialogue dialogue : this.dialogueList) {
            if (dialogue.id() == id) {
                return dialogue;
            }
        }
        return null;
    }

    public boolean processAction(Dialogue action) {
        String actionType = action.type();

        switch (actionType) {
            case "show_sprite":
                showSprite(action.sprite());
                break;

            case "dialogue":
                showDialogue(
                        action.label(),
                        action.content()
                );
                break;

            case "modify_variable":
                modifyVariable(
                        action.var(),
                        action.action(),
                        action.value()
                );
                break;

            case "give_item":
                giveItem(
                        action.itemId(),
                        action.amount()
                );
                break;

            case "conditional":
                processConditional(action);
                break;

            case "transition":
                if ("jump".equals(action.action())) {
                    processJump(action);
                }
                break;

            case "choice":
                showChoices(action.choice());
                break;

            case "command":
                processCommand(action);
                break;

            case "label":
                this.currentId++;
                break;

            case "finish_dialogue":
                return true;

            default:
                this.currentId++;
                break;
        }
        return false;
    }


    public void showSprite(String spritePath) {
        System.out.printf("\n[Showing sprite: %s]\n", spritePath);
        this.currentId++;
    }

    public void showDialogue(String label, String content) {
        System.out.printf("\n%s: %s", label, content);
        scanner.nextLine(); // For input()
        this.currentId++;
    }

    public String processCommand(Command command) {
        String action = command.action();
        switch (action) {
            case "get_gamemode":
                return "Survival";
            case "custom_command":
                return "Nothing for now";
            default:
                return "Nothing for now";
        }
    }

    public void modifyVariable(String variable, String operation, Object value) {
        if (value instanceof Command) {
            value = processCommand((Command) value);
        }

        switch (operation) {
            case "increment_var":
                if (variables.get(variable) instanceof Number && value instanceof Number) {
                    variables.put(variable, ((Number) variables.get(variable)).doubleValue() +
                            ((Number) value).doubleValue());
                }
                break;
            case "substract_var":
                if (variables.get(variable) instanceof Number && value instanceof Number) {
                    variables.put(variable, ((Number) variables.get(variable)).doubleValue() -
                            ((Number) value).doubleValue());
                }
                break;
            default:
                variables.put(variable, value);
        }
        this.currentId++;
    }

    public void giveItem(String item, int amount) {
        System.out.printf("\n[Received %dx %s]\n", amount, item);
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
        this.currentId++;
    }

    public void processJump(Map<String, Object> action) {
        this.currentId = findLabelId((String) action.get("label"));
    }

    public void processConditional(Map<String, Object> condition) {
        Object var = variables.get(condition.get("var"));
        Object value = condition.get("value");
        int end = (Integer) condition.get("end");

        if (value instanceof Map) {
            value = processCommand((Map<String, Object>) value);
        }

        System.out.println(value);
        System.out.println(var);

        String conditionType = (String) condition.get("condition");
        boolean result = false;

        if (var instanceof Number && value instanceof Number) {
            double numVar = ((Number) var).doubleValue();
            double numValue = ((Number) value).doubleValue();

            switch (conditionType) {
                case "equal":
                    result = numVar == numValue;
                    break;
                case "not_equal":
                    result = numVar != numValue;
                    break;
                case "less_than":
                    result = numVar < numValue;
                    break;
                case "greater_than":
                    result = numVar > numValue;
                    break;
            }
        } else {
            switch (conditionType) {
                case "equal":
                    result = var.equals(value);
                    break;
                case "not_equal":
                    result = !var.equals(value);
                    break;
            }
        }

        this.currentId = result ? this.currentId + 1 : end;
    }

    @SuppressWarnings("unchecked")
    public void showChoices(List<Map<String, Object>> choices) {
        System.out.println("\nChoices:");
        for (int i = 0; i < choices.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, choices.get(i).get("display"));
        }

        while (true) {
            try {
                System.out.println("\nEnter your choice (number): ");
                int choice = Integer.parseInt(scanner.nextLine()) - 1;
                if (choice >= 0 && choice < choices.size()) {
                    String targetLabel = (String) choices.get(choice).get("label");
                    this.currentId = findLabelId(targetLabel);
                    break;
                }
            } catch (NumberFormatException e) {
                // Handle parse error
            }
            System.out.println("Invalid choice. Please try again.");
        }
    }

    public void createVariable(String varName, Object varInit) {
        variables.put(varName, varInit);
        this.currentId++;
    }

    public void processMeta(Dialogue action) {
        String actionType = (String) action.action();
        if ("create_var".equals(actionType)) {
            createVariable((String) action.var(), action.init());
        } else {
            this.currentId++;
        }
    }


}
