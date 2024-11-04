package org.arsparadox.mobtalkerredux.vn.view;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.arsparadox.mobtalkerredux.vn.controller.VisualNovelEngine;
import org.arsparadox.mobtalkerredux.vn.data.DialogueState;
import org.arsparadox.mobtalkerredux.vn.data.SpriteState;

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

    private List<SpriteState> spritesToRender = new ArrayList<>();
    private VisualNovelEngine vn;
    private String label;
    private String content;
    private List<Map<String, Object>> choices;
    private String background;



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
        //if()
        updateSprites(state);
        content = state.getContent();
        choices = state.getChoices();
    }

    public void updateSprites(DialogueState state){
//        SpriteState currentSprite = state.getSprite();
//        for (SpriteState sprite: this.spritesToRender) {
//            if(Objects.equals(sprite.getSprite(), currentSprite.getSprite())){
//                removeSpriteByFolder(this.spritesToRender,sprite.getSprite());
//                break;
//            }
//        }
        spritesToRender = state.getSprites();
        //System.out.println(state.getSprites().size());

    }
    public void removeSpriteByFolder(List<SpriteState> sprites, String folderName) {
        sprites.removeIf(sprite -> sprite.getSprite().equals(folderName));
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
        vn.isEngineRunning=true;
        vn.runEngine();
        update();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        // Update content as needed
        renderBackground(poseStack);
        renderCharacterName(poseStack);
        renderForeground(poseStack);
        renderDialogueBox(poseStack);
//        dialogueBox.setContent(this.content); Still working on this bad boy
//        dialogueBox.render(poseStack); I want modularity, but I like my sanity intact
        renderChoiceButtons();
        if(vn.shutdown){
            onClose();
        }

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
//    @Override
//    public void renderBackground(PoseStack guiGraphics) {
//        if(background!=null&&!background.isEmpty()){
//            ResourceLocation bg = new ResourceLocation("mobtalkerredux",background);
//            RenderSystem.setShaderTexture(0, bg);
//            blit(guiGraphics,bg, 0, 0, 0, 0,0);
//        }
//
//    }

    public void renderForeground(PoseStack poseStack){

        // MINECRAFT RENDERING SYSTEM IS A NIGHTMARE!!!
        // FUCK, I have to make this BS
        //  ___ ___ ___ ___ ___
        // |___|___|___|___|___|
        // |___|___|HHH|___|___|
        // |___|___|HHH|___|___|
        // Pictured (5x3, 1x2, 3x2)
        // Okay so, all images will be defined by their aspect ratio WxH
        // So now I just have to make a logic that fits the thing in this thing
        // Because there's only so much way you can fit a 3x2 images inside a 5x3 rectangle
        // Yeah...
        // So in the FSM, determining position should be like:
        // (Screen Ratio, Image Ratio, Coordinate Position) -> (16x9, 3x5, 8x1)
        // And then I code the calculation in Minecraft!!
        // This should work, right??? Gods, I don't want to make Script Maker deal with this math...
        // I'll let mod maker (me) do the math...


        for (SpriteState sprite : spritesToRender) {
            ResourceLocation currentSprite = new ResourceLocation(
                    "mobtalkerredux", "textures/" + sprite.getLocation()
            );
            RenderSystem.setShaderTexture(0, currentSprite);

            double wRatio = sprite.getwRatio(); // Also the number of column
            double hRatio = sprite.gethRatio(); // Also the number of row

            double frameWRatio =sprite.getFrameWRatio(); // This will be the size of the 'frame' that does the render
            double frameHRatio = sprite.getFrameHRatio(); // Like the space the image took

            double startColumn = sprite.getStartColumn();
            double startRow = sprite.getStartRow(); //First row, we don't do zero, this isn't an array

            // Okay, stuff above  is what the script maker decide.

            // Now to math this shit

            int wBlocks = (int) (this.width/wRatio); //Actual Pixel Size of the Screen
            int hBlocks = (int) (this.height/hRatio); //Actual Pixel Size of the Screen

            int wThingBlock = (int) (wBlocks*frameWRatio);
            int hThingBlock = (int) (hBlocks*frameHRatio);

            int startColumnPos = (int) (wBlocks*(startColumn-1));
            int startRowPos = (int) (hBlocks*(startRow-1)); // Immediately regretted my decision there...

            // Fuck, these aren't squares aren't they? Shit...
            // Screw it, we'll see how this'll look like, then complain
            // Okay, those are positioning, the frame size... Next up is... Image Dimensions

            // Now how do we 'Fit' this fucker???

            blit(
                    poseStack, // The Thing
                    (int)startColumnPos, // The x location, I think it's the
                    (int)startRowPos, // The y location
                    0,  // source x I don't know what this does...
                    0,  // source y Oh nyooooo~
                    (int)wThingBlock,   // What even is this?
                    (int)hThingBlock,  // No seriously what is this???
                    (int)wThingBlock,  // What's the difference!?
                    (int)hThingBlock  // FUCK!!!
            );
        }
    }

    public void renderCharacterName(PoseStack poseStack) {
        if(label!=null){
            // Add a dark background behind the name for better readability
            int nameWidth = this.font.width(label);
            int nameX = (this.width - nameWidth) / 2;
            int nameY = (this.height - DISPLAYED_SPRITE_HEIGHT) / 3 - CHARACTER_NAME_OFFSET;

            // Draw name background
            fill(poseStack,
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
            fill(poseStack,labelBoxX, labelBoxY, labelBoxX + labelBoxWidth, labelBoxY + labelBoxHeight, backgroundColor);


            // Render label text centered in label box
            int labelWidth = this.font.width(label);
            int labelX = labelBoxX + (labelBoxWidth - labelWidth) / 2; // Center the label horizontally
            int labelY = labelBoxY + (labelBoxHeight - this.font.lineHeight) / 2; // Center the label vertically
            drawString(poseStack,this.font, label, labelX, labelY, 0xFFFFFF); // White text color

            // Render dialogue text with word wrapping
            List<String> wrappedText = wrapText(content, boxWidth - (DIALOGUE_BOX_PADDING * 2));
            int textY = boxY + DIALOGUE_BOX_PADDING;

            for (String line : wrappedText) {
                drawString(poseStack,this.font, line, boxX + DIALOGUE_BOX_PADDING, textY, 0xFFFFFF); // White text color
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