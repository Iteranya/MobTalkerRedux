package org.arsparadox.mobtalkerredux.vn.view;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.arsparadox.mobtalkerredux.vn.controller.VisualNovelEngine;
import org.arsparadox.mobtalkerredux.vn.data.DialogueState;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DialogueScreen extends Screen{
    private static final int DIALOGUE_BOX_PADDING = 15;
    private static final int CHARACTER_NAME_OFFSET = 40;
    private static final int SPRITE_WIDTH = 530/3;  // Original sprite width
    private static final int SPRITE_HEIGHT = 930/3; // Original sprite height
    private static final int DISPLAYED_SPRITE_WIDTH = 200;  // How wide we want it on screen
    private static final int DISPLAYED_SPRITE_HEIGHT = 300; // How tall we want it on screen
    private static final int CHOICE_BUTTON_WIDTH = 200;
    private static final int CHOICE_BUTTON_HEIGHT = 20;
    private static final int CHOICE_BUTTON_SPACING = 5;
    private int dialogueBoxHeight = 80;
    private List<Button> choiceButtons = new ArrayList<>();

    private VisualNovelEngine vn;
    private String label;
    private String content;
    private ResourceLocation sprite;
    private List<Map<String, Object>> choices;
    //private DialogueBoxComponent dialogueBox;



    public DialogueScreen(VisualNovelEngine vn) throws FileNotFoundException {
        super(Component.empty());;
        this.vn = vn;
        //dialogueBox = new DialogueBoxComponent();
    }

    @Override
    protected void init() {
        // Initialize the display
        startScene();
    }

    public void update(){
        DialogueState state = vn.getNext();
        label = state.getLabel();
        sprite = state.getSprite();
        content = state.getContent();
        choices = state.getChoices();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            vn.isEngineRunning=true;
            vn.runEngine();
            //Tell Engine to update the Globals, as in like, get the current state and put it in the global in this class
            update();
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void startScene() {
        vn.isEngineRunning=true;
        vn.runEngine();//run the engine, let it do some loops and backflips, engine's globals shouldn't affect this sacred place
        update();
    }

    private void onPress(String choice) { // BUTTON press,  btw
        vn.buttonPress(choice);//Tell engine to update their globals then update this class's global
        //After clicking, it tries to load the next dialogue...
        vn.state.emptyChoices();
        vn.isEngineRunning=true;
        vn.runEngine();
        update();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        // Update content as needed
        //renderBackground(poseStack);
        renderCharacterName(poseStack);
        renderCharacterSprite(poseStack);
        renderDialogueBox(poseStack);
//        dialogueBox.setContent(this.content); Still working on this bad boy
//        dialogueBox.render(poseStack); I want modularity, but I like my sanity intact
        renderChoiceButtons();
        if(vn.shutdown){
            onClose();
        }

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    public void renderCharacterSprite(PoseStack poseStack) {
        if(sprite!=null){
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

    }

    public void renderCharacterName(PoseStack poseStack) {
            if(label!=null){
                // Add a dark background behind the name for better readability
                int nameWidth = this.font.width(label);
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
                        label,
                        this.width / 2,
                        nameY,
                        0xFFFFFF
                );
            }



    }

    public void renderDialogueBox(PoseStack poseStack) {
        if (content != null) {
            // Set dialogue box dimensions and position
            int boxWidth = Math.min(600, this.width - 40); // Max width of 600 or screen width - 40
            int boxX = (this.width - boxWidth) / 2;
            int boxY = this.height - dialogueBoxHeight - 5; // 20 pixels from bottom
            int borderColor = 0xFF004400; // Border color with full opacity
            int backgroundColor = 0xCC000000; // Semi-transparent background color

            // Draw the border
            fill(poseStack, boxX - 1, boxY - 1, boxX + boxWidth + 1, boxY + dialogueBoxHeight + 1, borderColor);

            // Draw the main dialogue box background
            fill(poseStack, boxX, boxY, boxX + boxWidth, boxY + dialogueBoxHeight, backgroundColor);

            // Draw label box at the top of the dialogue box
            int labelBoxHeight = 20; // Height of the label box
            int labelBoxWidth = boxWidth / 5; // Width of the label box (1/5 of the dialogue box width)
            int labelBoxX = boxX; // Align label box with the left of the dialogue box
            int labelBoxY = boxY - labelBoxHeight - 5; // Position label box slightly above the dialogue box
// Draw the label box
            fill(poseStack, labelBoxX, labelBoxY, labelBoxX + labelBoxWidth, labelBoxY + labelBoxHeight, backgroundColor);


            // Render label text centered in label box
            int labelWidth = this.font.width(label);
            int labelX = labelBoxX + (labelBoxWidth - labelWidth) / 2; // Center the label horizontally
            int labelY = labelBoxY + (labelBoxHeight - this.font.lineHeight) / 2; // Center the label vertically
            drawString(poseStack, this.font, label, labelX, labelY, 0xFFFFFF); // White text color

            // Render dialogue text with word wrapping
            List<String> wrappedText = wrapText(content, boxWidth - (DIALOGUE_BOX_PADDING * 2));
            int textY = boxY + DIALOGUE_BOX_PADDING;

            for (String line : wrappedText) {
                drawString(poseStack, this.font, line, boxX + DIALOGUE_BOX_PADDING, textY, 0xFFFFFF); // White text color
                textY += this.font.lineHeight + 2;
            }
        }
    }
    public void renderChoiceButtons() {
        choiceButtons.forEach(this::removeWidget);
        choiceButtons.clear();
        if(choices!=null){
            // Clear existing buttons

            if (choices.isEmpty()) return;

            // Calculate total height of all buttons including spacing
            int totalButtonsHeight = (CHOICE_BUTTON_HEIGHT + CHOICE_BUTTON_SPACING) * choices.size() - CHOICE_BUTTON_SPACING;

            // Start position for first button
            int startY = this.height - dialogueBoxHeight - 40 - totalButtonsHeight;
            int buttonX = (this.width - CHOICE_BUTTON_WIDTH) / 2;
            int i = 0;
            for (Map<String, Object> choice: choices) {

                int buttonY = startY + (CHOICE_BUTTON_HEIGHT + CHOICE_BUTTON_SPACING) * i;

                Button button = new Button(
                        buttonX,
                        buttonY,
                        CHOICE_BUTTON_WIDTH,
                        CHOICE_BUTTON_HEIGHT,
                        Component.literal((String) choice.get("display")), // Use Component.literal instead of TextComponent
                        btn -> onPress((String) choice.get("label"))
                );

                choiceButtons.add(button);
                this.addRenderableWidget(button);
                i++;
            }
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
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(null);
    }


}
