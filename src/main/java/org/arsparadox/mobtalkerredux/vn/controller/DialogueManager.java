package org.arsparadox.mobtalkerredux.vn.controller;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.fml.loading.FMLConfig;
import org.arsparadox.mobtalkerredux.vn.data.dialogue.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DialogueManager {
    private final DialogueUpdater updater;
    private List<ChoiceItem> currentChoices = new ArrayList<>();
    private List<Variable> variables = new ArrayList<>();

//    private DialogueBoxComponent dialogueBoxComponent;
//    private ImageComponent imageComponent;
//    private ForegroundComponent foregroundComponent;
//    private BackgroundComponent backgroundComponent;
    private final List<Dialogue> dialogueList;

    private int currentId = 0;

    // This is the part where I replicate the FSM Loader Machine in Python into Java

    // Ugh... my head...

    public DialogueManager(DialogueList dialogueList, DialogueUpdater updater) {
        this.dialogueList = dialogueList.dialogueList();
        this.updater = updater;
        // Start with the first dialogue entry if available
        if (!dialogueList.dialogueList().isEmpty()) {
            updateDialogue();
        }
    }

    public void updateDialogue(){
    }

    public void update(PoseStack poseStack) {
        // Update the display with current state
        Dialogue dialogue = findDialogueById(this.currentId);
        processAction(dialogue,poseStack);
    }

    public Dialogue findDialogueById(int id){
        for (Dialogue dialogue : this.dialogueList) {
            if (dialogue.id() == id) {
                return dialogue;
            }
        }
        return null;
    }

    public Integer findLabelId(String var) {
        for (Dialogue action : this.dialogueList) {
            if ("label".equals(action.type()) && var.equals(action.label())) {
                return action.id();
            }
        }
        return null;
    }


    public boolean processAction(Dialogue action,PoseStack poseStack) {
        String actionType = action.type();

        switch (actionType) {
            case "show_sprite":
                showSprite(
                        action.sprite(),
                        poseStack
                );
                break;

            case "dialogue":
                showDialogue(
                        action.label(),
                        action.content(),
                        poseStack
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
                updateChoice(action.choice());
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

    public void updateChoice(List<ChoiceItem> choices){
        updater.setChoices(choices);

    }


    public void showSprite(String spritePath, PoseStack poseStack) {
        String sprite = FMLConfig.defaultConfigPath()+spritePath;
        //Update sprite here
        updater.displayCharacter(poseStack,sprite);
        this.currentId++;
    }

    public void showDialogue(String label, String content,PoseStack poseStack) {
        //Update Dialogue Box here
        updater.updateDialogue(poseStack,content,label);
        this.currentId++;
    }

    public String processCommand(Object command) {
        String action="";
        if(command instanceof Command){
            action = ((Command) command).action();
        }
        if(command instanceof Dialogue){
            action = ((Dialogue) command).action();
        }

        switch (action) {
            case "get_gamemode":
                return "Survival";
            case "custom_command":
                return "Nothing for now";
            default:
                return "Nothing for now";
        }
    }

    public Variable findVariableByName(String name){
        for (Variable variable : this.variables){
            if (Objects.equals(variable.name(), name)){
                return variable;
            }
        }
        return null;
    }

    public List<Variable> removeVariableByName(String nameToRemove) {
        List<Variable> resultingList = new ArrayList<>(this.variables);
        resultingList.removeIf(variable -> variable.name().equals(nameToRemove));
        return resultingList;
    }


    public void modifyVariable(String variable, String operation, Object value) {
        if (value instanceof Command) {
            value = processCommand((Command) value);
        }
        Variable var = findVariableByName(variable);
        switch (operation) {
            case "increment_var":
                if (var.value() instanceof Number && value instanceof Number) {
                    this.variables = removeVariableByName(var.name());
                    Integer newValue = (Integer) var.value();
                    newValue++;
                    this.variables.add(new Variable(variable,newValue));
                }
                break;
            case "substract_var":
                if (var.value() instanceof Number && value instanceof Number) {
                    this.variables = removeVariableByName(var.name());
                    Integer newValue = (Integer) var.value();
                    newValue--;
                    this.variables.add(new Variable(variable,newValue));
                }
            default:
                variables.add(new Variable(variable, value));
        }
        this.currentId++;
    }

    public void giveItem(String item, int amount) {
        //Process this with Minecraft Command
        this.currentId++;
    }

    public void processJump(Dialogue action) {
        this.currentId = findLabelId((String) action.label());
    }

    public void processConditional(Dialogue condition) {
        Object var="";
        for (Variable variable:variables) {
            if(Objects.equals(variable.name(), condition.var())){
                var = variable.value();
            }
        }
        Object value = condition.value();
        int end = condition.end();

        if (value instanceof Command) {
            value = processCommand((Command) value);
        }

        System.out.println(value);
        System.out.println(var);

        String conditionType = condition.condition();
        boolean result = false;

        if (var instanceof Number && value instanceof Number) {
            double numVar = ((Number) var).doubleValue();
            double numValue = ((Number) value).doubleValue();

            switch (conditionType) {
                case "equal" -> result = numVar == numValue;
                case "not_equal" -> result = numVar != numValue;
                case "less_than" -> result = numVar < numValue;
                case "greater_than" -> result = numVar > numValue;
            }
        } else {
            result = switch (conditionType) {
                case "equal" -> var.equals(value);
                case "not_equal" -> !var.equals(value);
                default -> false;
            };
        }

        this.currentId = result ? this.currentId + 1 : end;
    }




    public void createVariable(String varName, Object varInit) {
        variables.add(new Variable(varName,varInit));
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
