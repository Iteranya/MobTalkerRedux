package org.arsparadox.mobtalkerredux.vn.controller;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import org.arsparadox.mobtalkerredux.vn.data.dialogue.ChoiceItem;

import java.util.List;

public interface DialogueUpdater {
    void updateDialogue(PoseStack poseStack, String dialogue,String characterName);
    void setChoices(PoseStack poseStack,List<ChoiceItem> choices);
    void displayCharacter(PoseStack poseStack,ResourceLocation sprite);
}
