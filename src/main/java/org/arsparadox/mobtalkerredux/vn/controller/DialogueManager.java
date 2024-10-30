package org.arsparadox.mobtalkerredux.vn.controller;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import org.arsparadox.mobtalkerredux.vn.data.dialogue.ChoiceItem;
import org.arsparadox.mobtalkerredux.vn.data.dialogue.Dialogue;
import org.arsparadox.mobtalkerredux.vn.data.dialogue.DialogueList;

import java.util.ArrayList;
import java.util.List;

public class DialogueManager {
    private final DialogueUpdater updater;
    private DialogueUpdater dialogueUpdater;
    private String currentCharacterName = "";
    private String currentDialogue = "";
    private ResourceLocation currentSprite = null;
    private List<ChoiceItem> currentChoices = new ArrayList<>();

//    private DialogueBoxComponent dialogueBoxComponent;
//    private ImageComponent imageComponent;
//    private ForegroundComponent foregroundComponent;
//    private BackgroundComponent backgroundComponent;
    private final List<Dialogue> dialogueList;

    public DialogueManager(DialogueList dialogueList, DialogueUpdater updater) {
        this.dialogueList = dialogueList.dialogueList();
        this.updater = updater;
        // Start with the first dialogue entry if available
        if (!dialogueList.dialogueList().isEmpty()) {
            initializeDialogue();
        }
    }

    public void update(PoseStack poseStack) {
        // Update the display with current state
        if (currentSprite != null) {
            updater.displayCharacter(poseStack, currentSprite);
        }

        if (!currentDialogue.isEmpty()) {
            updater.updateDialogue(poseStack, currentDialogue, currentCharacterName);
        }

        if (!currentChoices.isEmpty()) {
            updater.setChoices(poseStack, currentChoices);
        }
    }


}
