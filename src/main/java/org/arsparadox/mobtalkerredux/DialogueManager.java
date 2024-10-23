package org.arsparadox.mobtalkerredux;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DialogueManager {
    private List<Dialogue> dialogues;
    private int currentDialogue = 1;

    public DialogueManager(List<Dialogue> dialogues) {
        this.dialogues = dialogues;
    }

    public void proceedToNextDialogue(Dialogue dialogue) {
        this.currentDialogue = dialogue.getNext();
    }

    public void proceedToChosenDialogue(int nextDialogId) {
        this.currentDialogue = nextDialogId;

    }

    public Optional<Dialogue> getCurrentDialogue() {
        return dialogues.stream()
                .filter(dialogue -> dialogue.getDialogueId().equals(currentDialogue))
                .findFirst();
    }

}

// Dialogue class representing a single dialogue entry
class Dialogue {
    private Integer dialogueId;
    private String content;
    private List<Choice> choices;

    private Integer nextDialogue;

    public Dialogue(Integer dialogueId, String content, List<Choice> choices, Integer next) {
        this.dialogueId = dialogueId;
        this.content = content;
        this.choices = choices;
        this.nextDialogue = Objects.requireNonNullElseGet(next, () -> dialogueId);
    }

    public String getContent() {
        return content;
    }
    public Integer getDialogueId(){return this.dialogueId;}
    public Integer getNext(){return this.nextDialogue;}

    public List<Choice> getChoices() {
        return choices == null ? List.of() : choices;
    }
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
