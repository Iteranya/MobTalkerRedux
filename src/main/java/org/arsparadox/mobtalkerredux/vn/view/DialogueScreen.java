package org.arsparadox.mobtalkerredux.vn.view;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
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
        spritesToRender = state.getSprites();
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

    public GuiGraphics processAnimation(GuiGraphics poseStack, int wBlock, int hBlock){
        // WELL FUCK GUESS WE'RE DOING ANIMATIONS NOW!!!
        // SHIT!!! FUCK!!! GOD WHY!?

        //Okay, no studio quality professional grade animation...
        // JUST SOMETHING SIMPLE
        // SOMTHING RESEMBLING AN ANIMATION, YEA!?
        // okay... nyaaa...
        // ...
        // I want to be cat...
        // Anyway...

        // Translation right???
        // poseStack.pose().translate(x, y, z);
        // What do I want for Translation???
        // X, Y, Z, right??? That's it... Nothing Weird...
        // So...
        // Let's do that, X, Y, Z

        // No, not  X, Y, Z
        // Simplify it you FUCK!
        // Left Right, Up Down
        // X and Y, that's IT!
        // Just because the function wants a Z doesn't mean we should give it!

        // Now, how far???, nay, what should the script maker care about?
        // Script Makers are FUCKING HUMAN they don't see things in Pixels, they barely know what a Kilometers are
        // So... (Deep Breath)
        // Block Distance.

        int moveRowDistance = 1; // 2 blocks worth of distance right...
        int moveColDistance = 3; // 2 blocks worth of distance down...
        int moveRowDistanceInPixels = moveRowDistance*wBlock; // 1 blocks worth of distance right...
        int moveColDistanceInPixels = moveColDistance*hBlock; // 3 blocks worth of distance down...

        // Okay that's translation distance...
        // Now for the 'Timing' the Glorious 'Timing'

        float animationTime = 500f; // How long it takes to travel 2 blocks worth of distance 1000 milis  is a second
        // Should we add like... Ease In, Ease Out...
        // NO! FUCK NO! (later) YOU LITTLE FUCK! YOU HAVEN'T EVEN FIGURE THIS OUT YET!!!
        float translateTime = (System.currentTimeMillis() % (int)animationTime) / animationTime; // Math magic, I guess
        float offsetX = moveRowDistanceInPixels * translateTime;// Just multiply to get current position
        float offsetY = moveColDistanceInPixels * translateTime;

        poseStack.pose().pushPose(); // Start animation block
        poseStack.pose().translate(offsetX, offsetY, 0);// Go Nyooom To Bottom Left Corner

        return poseStack;
        // The Following is under construction. Let's go with Translation first...
        // Rotate and scaling can wait.
        // Now, how about scale and nyoomin? I mean zoomin
        // Uhhh
        // poseStack.pose().scale(a,b,c);
        // Day 57 questioning why Minecraft Don't Put Proper Parameter Names
        // What the fuck is a? What the fuck is b? What the fuck is c?
        // Oh nevermind, same shit, x,y,z
        // Okay width scaling and height scaling, we can pretend z don't exist

        // So like... We take the old block dimension, say uhh... 5x8
        // and then scale it
        // For example
//        float oldThingBlockWidth = 5f;
//        float oldThingBlockHeight = 8f;
//        float newThingBlockWidth = 5.5f;
//        float newThingBlockHeight = 8.5f;
//
//        // Then, I want to turn this into a certain dimension...
//        // God... I have TO MATH, FUCK!
//        // So uhh... newWidth = oldWidth*scale
//        // scale = newWidth/oldWidth
//        // Hell yeah! Elementary Algebra Baby, WOOOOO!!!
//
//        // Wait, so I expect Script Maker to decide the resulting size???
//        // Is that more intuitive?? I mean, if you think about it...
//        // This is more responsive, right? Maybe?? Ah, screw it, better this than nothing.
//
//        float widen = newThingBlockWidth/oldThingBlockWidth;
//        float heighten = newThingBlockHeight/oldThingBlockHeight;
//        // This should work right??
//        // Wait... WAIT! Scale Through TIME isn't it!?
//        // Oh nyooooo~
//        // Wait, the translate pose doesn't take the result it takes the thingy to add
//        // Ahhhhh... I have to redo everything!!!
//        // Screw it, I need to grab coffee I'm committing this first...
//        // Aight, let's try widen*currenttime
//
//        // Okay, that actually works, THANK FUCK
//        // Anyway, it starts at zero though
//        // So it Cupa just go nyoooom~ from a distance
//        // Hilarious, but I want it to start scaling from the current size...
//        // Okay, next is the rotate around function...
//        // Uhh... Fuck, how do I do this???
//        // I dunno... Maybe uhh...
//        float widthScaleTime = 1 + ((System.currentTimeMillis() % 500) / 500f) * (widen - 1); // Modify scaleTime for width
//        float heightScaleTime = 1 + ((System.currentTimeMillis() % 500) / 500f) * (heighten - 1); // Modify scaleTime for height
//
//        poseStack.pose().scale(widthScaleTime,heightScaleTime,0);
//
//        // This somewhat works
//        // I need to pair this with a translate function though...
//        // Eh, later I guess...
//
//        //poseStack.pose().rotateAround((float) Math.toRadians(90), 0, 1, 0);
//        // Apparently that will rotate the thing in y axis
//        // Excuse me what the fuck is a y axis?
//        // Just rotate? Why do we need axis to rotate!?
//        // ^utter lack of animation / rendering experience
//        Quaternionf quaternion = new Quaternionf().rotateAxis((float) Math.toRadians(90), 0,0, 0);
//        //poseStack.pose().rotateAround(quaternion,0,0,0);
//        return poseStack;
//        // Now how the fuck do I make this shit modular???
    }

    @Override
    public void render(GuiGraphics poseStack, int mouseX, int mouseY, float partialTicks) {
        // Update content as needed


        renderBackground(poseStack);
        renderCharacterName(poseStack);
        renderForegroundWithAnimation(poseStack);
        renderDialogueBox(poseStack);
//        dialogueBox.setContent(this.content); Still working on this bad boy
//        dialogueBox.render(poseStack); I want modularity, but I like my sanity intact
        renderChoiceButtons();
        if(vn.shutdown){
            onClose();
        }

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
    @Override
    public void renderBackground(GuiGraphics guiGraphics) {
        if(background!=null&&!background.isEmpty()){
            ResourceLocation bg = new ResourceLocation("mobtalkerredux",background);
            RenderSystem.setShaderTexture(0, bg);
            guiGraphics.blit(bg, 0, 0, 0, 0, this.width, this.height);
        }

    }

    public void renderForegroundWithAnimation(GuiGraphics poseStack){

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
            //if(sprite.getAnimationStatus()) {
                // Another Nightmare Under Progress
                poseStack = processAnimation(poseStack, wBlocks, hBlocks); //DO NOT TOGGLE IN PRODUCTION
            //}
            poseStack.blit(
                    currentSprite, // The image to show on screen
                    startColumnPos, // I refuse to call this x, this is a COLUMN
                    startRowPos, //  I refuse to call this y, this is a ROW
                    0, // I have No Bloody Clue
                    0, // What the fuck this does
                    wThingBlock, // Put Image Dimension, the width
                    hThingBlock, // Put Image Dimension, the height
                    wThingBlock, // Put it again, I guess
                    hThingBlock // Fuck if I know what this does, it works
            );
            sprite.disableAnimation();
            poseStack.pose().popPose(); // End animation block
        }
    }


    public void renderCharacterName(GuiGraphics poseStack) {
            if(label!=null){
                // Add a dark background behind the name for better readability
                int nameWidth = this.font.width(label);
                int nameX = (this.width - nameWidth) / 2;
                int nameY = (this.height - DISPLAYED_SPRITE_HEIGHT) / 3 - CHARACTER_NAME_OFFSET;

                // Draw name background
                poseStack.fill(
                        nameX - DIALOGUE_BOX_PADDING,
                        nameY - 2,
                        nameX + nameWidth + DIALOGUE_BOX_PADDING,
                        nameY + this.font.lineHeight + 2,
                        0x88000000
                );

                // Draw name
                poseStack.drawCenteredString(
                        this.font,
                        label,
                        this.width / 2,
                        nameY,
                        0xFFFFFF
                );
            }



    }

    public void renderDialogueBox(GuiGraphics poseStack) {
        if (content != null) {
            // Set dialogue box dimensions and position
            int boxWidth = Math.min(600, this.width - 40); // Max width of 600 or screen width - 40
            int boxX = (this.width - boxWidth) / 2;
            int boxY = this.height - dialogueBoxHeight - 5; // 20 pixels from bottom
            int borderColor = 0xFF004400; // Border color with full opacity
            int backgroundColor = 0xCC000000; // Semi-transparent background color

            // Draw the border
            poseStack.fill( boxX - 1, boxY - 1, boxX + boxWidth + 1, boxY + dialogueBoxHeight + 1, borderColor);

            // Draw the main dialogue box background
            poseStack.fill( boxX, boxY, boxX + boxWidth, boxY + dialogueBoxHeight, backgroundColor);

            // Draw label box at the top of the dialogue box
            int labelBoxHeight = 20; // Height of the label box
            int labelBoxWidth = boxWidth / 5; // Width of the label box (1/5 of the dialogue box width)
            int labelBoxX = boxX; // Align label box with the left of the dialogue box
            int labelBoxY = boxY - labelBoxHeight - 5; // Position label box slightly above the dialogue box
// Draw the label box
            poseStack.fill(labelBoxX, labelBoxY, labelBoxX + labelBoxWidth, labelBoxY + labelBoxHeight, backgroundColor);


            // Render label text centered in label box
            int labelWidth = this.font.width(label);
            int labelX = labelBoxX + (labelBoxWidth - labelWidth) / 2; // Center the label horizontally
            int labelY = labelBoxY + (labelBoxHeight - this.font.lineHeight) / 2; // Center the label vertically
            poseStack.drawString(this.font, label, labelX, labelY, 0xFFFFFF); // White text color

            // Render dialogue text with word wrapping
            List<String> wrappedText = wrapText(content, boxWidth - (DIALOGUE_BOX_PADDING * 2));
            int textY = boxY + DIALOGUE_BOX_PADDING;

            for (String line : wrappedText) {
                poseStack.drawString(this.font, line, boxX + DIALOGUE_BOX_PADDING, textY, 0xFFFFFF); // White text color
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
                Button button = Button.builder(
                        Component.literal((String) choice.get("display")),
                        btn -> onPress((String) choice.get("label"))

                ).pos(buttonX,buttonY).build();

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
