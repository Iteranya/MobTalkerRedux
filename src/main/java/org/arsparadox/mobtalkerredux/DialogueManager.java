package org.arsparadox.mobtalkerredux;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DialogueManager {
    private List<Dialogue> dialogues;

    private int currentDialogue;
    private boolean interactionAllowed;

    public DialogueManager(List<Dialogue> dialogues) {
        this.dialogues = dialogues;
        this.currentDialogue = 0;
        this.interactionAllowed = true;
    }

    public void proceedToNextDialogue(Dialogue dialogue) {
        this.currentDialogue = dialogue.getNext();
        //this.interactionAllowed = false;
    }

    public void proceedToChosenDialogue(int nextDialogId) {
        this.currentDialogue = nextDialogId;

    }

    public Optional<Dialogue> getCurrentDialogue() {
        return dialogues.stream()
                .filter(dialogue -> dialogue.getDialogueId().equals(currentDialogue))
                .findFirst();
    }

    public void allowInteraction() {
        this.interactionAllowed = true;
    }

    public boolean isInteractionAllowed(){
        return this.interactionAllowed;
    }

}

// Dialogue class representing a single dialogue entry
class Dialogue {
    private Integer dialogueId;
    private String content;
    private List<Choice> choices;

    private Integer nextDialogue;

    private String name;
    private ResourceLocation sprite;

    public Dialogue(Integer dialogueId, String content, List<Choice> choices, Integer next, String name, String sprite) {
        this.dialogueId = dialogueId;
        this.content = content;
        this.choices = choices;
        this.nextDialogue = Objects.requireNonNullElseGet(next, () -> -1);
        this.name =name;
        this.sprite = new ResourceLocation("mobtalkerredux", "textures/characters/"+sprite);
    }

    public String getContent() {return content;}
    public Integer getDialogueId(){return this.dialogueId;}
    public Integer getNext(){return this.nextDialogue;}
    public List<Choice> getChoices() {return choices == null ? List.of() : choices;}
    public ResourceLocation getSprite() {return this.sprite;}

    public String getName() {return this.name;}
}

// Choice class representing a choice in dialogues
class Choice {
    private String buttonText;
    private int affectionChange;
    private int nextDialogId;

    public Choice(String buttonText, int affectionChange, int nextDialogId) {
        this.buttonText = buttonText;
        this.affectionChange = affectionChange;
        this.nextDialogId = nextDialogId;
    }



    public String getButtonText() {
        return buttonText;
    }

    public int getAffectionChange() {
        return affectionChange;
    }

    public int getNextDialogId() {
        return nextDialogId;
    }
}
