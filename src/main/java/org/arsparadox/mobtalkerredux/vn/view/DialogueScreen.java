package org.arsparadox.mobtalkerredux.vn.view;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.arsparadox.mobtalkerredux.vn.controller.VisualNovelEngine;
import org.arsparadox.mobtalkerredux.vn.data.DialogueState;
import org.arsparadox.mobtalkerredux.vn.data.SpriteState;
import org.arsparadox.mobtalkerredux.vn.view.components.DialogueBoxManager;
import org.arsparadox.mobtalkerredux.vn.view.components.ForegroundComponent;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DialogueScreen extends Screen{
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
    private String command;
    private String music;
    private String sound;
    private SoundUtils se;
    public Mob mob;
    private Player player;

    private boolean hiddenDialogue = false;




    public DialogueScreen(VisualNovelEngine vn, LivingEntity target, Player player) throws FileNotFoundException {
        super(Component.empty());
        this.vn = vn;
        this.se = new SoundUtils();


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
        background = state.getBackground();
//        if(player.server.isSingleplayer()){
//            //ForgeCommandRunner.runCommand(player.server,command);
//        }

        if(state.getSound()!=null){
            if(!state.getSound().equals(sound)){
                sound = state.getSound();
                playSound(sound);
                System.out.println("Current  Sound: "+state.getSound());
            }
        }
       if(state.getMusic()!=null){
           if(!state.getMusic().equals(music)){
               music = state.getMusic();
               playMusic(music);
               System.out.println("Current  Music: "+music);
           }
       }else{
           playMusic(music);
           music = state.getMusic();
       }


    }

    public void playMusic(String music){
        if(music!=null){
            ResourceLocation musicPath = new ResourceLocation("mobtalkerredux","music."+music);
            se.playMusic(musicPath);
            System.out.println("Playing: "+music);
        }else{
            se.stopMusic();
        }

    }
    public void playSound(String sound){
        if(sound!=null){
            ResourceLocation soundPath = new ResourceLocation("mobtalkerredux","sound."+sound);
            se.playSound(soundPath);
            System.out.println("Playing: "+sound);
        }else{
            se.stopSound();
        }
    }

    public void updateSprites(DialogueState state){
        spritesToRender = state.getSprites();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if(!hiddenDialogue){
                vn.isEngineRunning.set(true);
                vn.runEngine();
                //Tell Engine to update the Globals, as in like, get the current state and put it in the global in this class
                update();
            }

        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 32 || keyCode == 257) {  // 32 = Space, 257 = Enter
            if(!hiddenDialogue){
                vn.isEngineRunning.set(true);
                vn.runEngine();
                update();
            }

        }
        else if(keyCode == 72){
            hiddenDialogue = !hiddenDialogue;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void startScene() {
        vn.isEngineRunning.set(true);
        vn.runEngine();//run the engine, let it do some loops and backflips, engine's globals shouldn't affect this sacred place
        update();
    }

    private void onPress(String choice) { // BUTTON press,  btw
        vn.buttonPress(choice);//Tell engine to update their globals then update this class's global
        //After clicking, it tries to load the next dialogue...
        vn.isEngineRunning.set(true);
        vn.runEngine();
        update();
    }

    @Override
    public void render(GuiGraphics poseStack, int mouseX, int mouseY, float partialTicks) {
        // Update content as needed
        renderBackground(poseStack);

        // Render the Sprites and Everything In Foreground
        if (spritesToRender != null && !spritesToRender.isEmpty()) {
            ForegroundComponent.processForeground(
                    poseStack, this.width, this.height, spritesToRender,vn.localVariables
            );
        }

        // Render the Dialogue Box
        if (content != null && !content.isEmpty() && !hiddenDialogue) {
            poseStack = DialogueBoxManager.processGui(
                    poseStack,this.width,this.height,content,label,this.font,vn.localVariables
            );
        }

        renderChoiceButtons();
        if(vn.shutdown.get()){
            onClose();
        }

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
    @Override
    public void renderBackground(GuiGraphics guiGraphics) {
        if(background!=null&&!background.isEmpty()){
            ResourceLocation bg = new ResourceLocation("mobtalkerredux","textures/"+background);
            //System.out.println("Tried to change background to: "+background);
            RenderSystem.setShaderTexture(0, bg);
            guiGraphics.blit(bg, 0, 0, 0, 0, this.width, this.height);
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

    @Override
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(null);
    }


}
