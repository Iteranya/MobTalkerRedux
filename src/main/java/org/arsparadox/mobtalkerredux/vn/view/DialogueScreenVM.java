package org.arsparadox.mobtalkerredux.vn.view;

import org.arsparadox.mobtalkerredux.model.Dialogue;
import org.arsparadox.mobtalkerredux.vn.controller.VNEngine;
import org.arsparadox.mobtalkerredux.vn.controller.VisualNovelFSM;

import java.io.FileNotFoundException;
import java.util.Optional;

public class DialogueScreenVM {

    String jsonPath;
    VNEngine vn;
    public DialogueScreenVM(String jsonPath) throws FileNotFoundException {
        this.jsonPath = jsonPath;
        this.vn = new VNEngine(jsonPath);
    }
    public Optional<Dialogue> getCurrentDialogue() {
        //This is where Dialogue is being streamed
        return dialogues.stream()
                .filter(dialogue -> dialogue.getDialogueId().equals(currentDialogue))
                .findFirst();
    }


    public Optional<Sprite> getCurrentSprite() {
        return actions.stream()
                .filter(dialogue -> dialogue.getSprite().equals(currentDialogue))
                .findFirst();
    }

    public Optional<CharacterName> getCharacterName() {
        return actions.stream()
                .filter(dialogue -> dialogue.getCharacterName().equals(currentDialogue))
                .findFirst();
    }

    public Optional<Content> getDialogue() {
        return actions.stream()
                .filter(dialogue -> dialogue.getContent().equals(currentDialogue))
                .findFirst();
    }

    public void Optional<Choice> getChoices() {
        return actions.stream()
                .filter(dialogue -> dialogue.getChoice().equals(currentDialogue))
                .findFirst();
    }

    public void getDialogueContent() {
    }


}

