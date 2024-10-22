package org.arsparadox.mobtalkerredux;

import java.util.List;
import java.util.Optional;

public class DialogueManager {
    private List<Dialogue> dialogues;
    private int currentDialogueIndex = 0;

    public DialogueManager(List<Dialogue> dialogues) {
        this.dialogues = dialogues;
    }

    public void proceedToNextDialogue() {
        if (currentDialogueIndex < dialogues.size() - 1) {
            currentDialogueIndex++;
        }
    }

    public void proceedToChosenDialogue(int nextDialogId) {
        currentDialogueIndex = nextDialogId;

    }

    public Optional<Dialogue> getCurrentDialogue() {
        if (currentDialogueIndex < dialogues.size()) {
            return Optional.of(dialogues.get(currentDialogueIndex));
        }
        return Optional.empty();
    }


}

// Dialogue class representing a single dialogue entry
class Dialogue {
    private String content;
    private List<Choice> choices;

    public Dialogue(String content, List<Choice> choices) {
        this.content = content;
        this.choices = choices;
    }

    public String getContent() {
        return content;
    }

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
