package org.arsparadox.mobtalkerredux;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import org.arsparadox.mobtalkerredux.lua.LuaBridge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DialogueScreen extends Screen implements DialogueManager.DialogueStateObserver {
    private static final int DIALOGUE_BOX_PADDING = 15;
    private static final int CHARACTER_NAME_OFFSET = 40;
    private static final int SPRITE_WIDTH = 530/4;  // Original sprite width
    private static final int SPRITE_HEIGHT = 930/4; // Original sprite height
    private static final int DISPLAYED_SPRITE_WIDTH = 200;  // How wide we want it on screen
    private static final int DISPLAYED_SPRITE_HEIGHT = 300; // How tall we want it on screen
    private static final int CHOICE_BUTTON_WIDTH = 200;
    private static final int CHOICE_BUTTON_HEIGHT = 20;
    private static final int CHOICE_BUTTON_SPACING = 5;
    private int dialogueBoxHeight = 80;
    private List<Button> choiceButtons = new ArrayList<>();
    private DialogueManager dialogueManager;
    private LuaBridge luaBridge;

    protected DialogueScreen(DialogueManager dialogueManager, LuaBridge luaBridge) {
        super(new TextComponent("Mob Talker"));
        this.dialogueManager = dialogueManager;
        this.luaBridge = luaBridge;
        this.dialogueManager.addObserver(this);
    }
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);

        var dialogueBoxStateOpt = dialogueManager.getDialogueBox();
        dialogueBoxStateOpt.ifPresent(dialogueBox->{
            renderDialogueBox(poseStack, dialogueBox.content());
        });

        var characterLabelStateOpt = dialogueManager.getCharacterLabel();
        characterLabelStateOpt.ifPresent(characterLabel->{
            renderCharacterName(poseStack, characterLabel.content());
        });

        var characterSpriteStateOpt = dialogueManager.getCharacterSprite();
        characterSpriteStateOpt.ifPresent(characterSprite->{
            ResourceLocation location = new ResourceLocation(
                    "textures/characters/cupa/"+characterSprite.content()
            );
            renderCharacterSprite(poseStack, location);
        });

        var choiceStateOpt = dialogueManager.getChoicesBox();
        choiceStateOpt.ifPresent(choiceState->{
            renderChoiceButtons(choiceState.choices());
        });

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    private void renderCharacterSprite(PoseStack poseStack, ResourceLocation sprite) {
        RenderSystem.setShaderTexture(0, sprite);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Calculate sprite position to center it
        int spriteX = (this.width - DISPLAYED_SPRITE_WIDTH) / 2;
        int spriteY = (this.height - DISPLAYED_SPRITE_HEIGHT) / 3; // Position it in upper third

        // Render the sprite with proper scaling
        blit(
                poseStack,
                spriteX,
                spriteY,
                0, // uOffset
                0, // vOffset
                DISPLAYED_SPRITE_WIDTH,
                DISPLAYED_SPRITE_HEIGHT,
                SPRITE_WIDTH,
                SPRITE_HEIGHT
        );

        RenderSystem.disableBlend();
    }

    private void renderCharacterName(PoseStack poseStack, String name) {
        // Add a dark background behind the name for better readability
        int nameWidth = this.font.width(name);
        int nameX = (this.width - nameWidth) / 2;
        int nameY = (this.height - DISPLAYED_SPRITE_HEIGHT) / 3 - CHARACTER_NAME_OFFSET;

        // Draw name background
        fill(
                poseStack,
                nameX - DIALOGUE_BOX_PADDING,
                nameY - 2,
                nameX + nameWidth + DIALOGUE_BOX_PADDING,
                nameY + this.font.lineHeight + 2,
                0x88000000
        );

        // Draw name
        drawCenteredString(
                poseStack,
                this.font,
                name,
                this.width / 2,
                nameY,
                0xFFFFFF
        );
    }

    private void renderDialogueBox(PoseStack poseStack, String dialogue) {
        // Calculate dialogue box dimensions
        int boxWidth = Math.min(600, this.width - 40); // Max width of 600 or screen width - 40
        int boxX = (this.width - boxWidth) / 2;
        int boxY = this.height - dialogueBoxHeight - 20; // 20 pixels from bottom

        // Draw dialogue box background with gradient
        fill(
                poseStack,
                boxX,
                boxY,
                boxX + boxWidth,
                boxY + dialogueBoxHeight,
                0xCC000000 // Base color
        );

        // Add border
        fill(
                poseStack,
                boxX - 1,
                boxY - 1,
                boxX + boxWidth + 1,
                boxY + dialogueBoxHeight + 1,
                0xFF004400 // Border color
        );

        // Word wrap and render dialogue text
        List<String> wrappedText = wrapText(dialogue, boxWidth - (DIALOGUE_BOX_PADDING * 2));
        int textY = boxY + DIALOGUE_BOX_PADDING;

        for (String line : wrappedText) {
            drawString(
                    poseStack,
                    this.font,
                    line,
                    boxX + DIALOGUE_BOX_PADDING,
                    textY,
                    0xFFFFFF
            );
            textY += this.font.lineHeight + 2;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click

            dialogueManager.displayNext();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void renderChoiceButtons(Map<String, String> choices) {
        // Clear existing buttons
        choiceButtons.forEach(this::removeWidget);
        choiceButtons.clear();

        if (choices.isEmpty()) return;

        // Calculate total height of all buttons including spacing
        int totalButtonsHeight = (CHOICE_BUTTON_HEIGHT + CHOICE_BUTTON_SPACING) * choices.size() - CHOICE_BUTTON_SPACING;

        // Start position for first button
        int startY = this.height - dialogueBoxHeight - 40 - totalButtonsHeight;
        int buttonX = (this.width - CHOICE_BUTTON_WIDTH) / 2;
        int i = 0;
        for (Map.Entry<String, String> choice : choices.entrySet()) {
            int buttonY = startY + (CHOICE_BUTTON_HEIGHT + CHOICE_BUTTON_SPACING) * i++;

            // Get the display text (value) from the map
            String displayText = choice.getValue();
            // Get the label (key) that will be passed to onPress
            String label = choice.getKey();

            Button button = new Button(
                    buttonX,
                    buttonY,
                    CHOICE_BUTTON_WIDTH,
                    CHOICE_BUTTON_HEIGHT,
                    new TextComponent(displayText),
                    btn -> dialogueManager.buttonNav(label)
            );

            choiceButtons.add(button);
            this.addRenderableWidget(button);
        }
    }

    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (this.font.width(currentLine + " " + word) <= maxWidth) {
                if (currentLine.length() > 0) currentLine.append(" ");
                currentLine.append(word);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
                currentLine.append(word);
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }
    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause the game
    }

    @Override
    public void renderBackground(PoseStack poseStack) {
        // Disable background darkening
    }

    @Override
    public void onDialogueStateChanged() {
        // Clear existing choice buttons when state changes
        choiceButtons.forEach(this::removeWidget);
        choiceButtons.clear();

        // Re-render choices if present
        dialogueManager.getChoicesBox().ifPresent(choiceState -> {
            renderChoiceButtons(choiceState.choices());
        });

        // Force a screen refresh
        // Note: In Minecraft, this will trigger the render method on the next frame
        this.minecraft.setScreen(this);
    }

    @Override
    public void removed() {
        super.removed();
        // Clean up by removing the observer when the screen is closed
        dialogueManager.removeObserver(this);
    }

}
