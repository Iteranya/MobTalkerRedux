package org.arsparadox.mobtalkerredux;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class MobTalkerGui extends Screen {

    private static final ResourceLocation SOLIAH = new ResourceLocation("mobtalkerredux", "textures/gui/soliah.png");

    protected MobTalkerGui() {
        super(new TextComponent("Mob Talker"));
    }

    @Override
    protected void init() {
        // Example adding buttons
        this.addRenderableWidget(new Button(this.width / 2 - 50, this.height / 6 + 96 - 6, 100, 20,
                new TextComponent("Humu!"), e -> onPress("Humu humu~")));
        this.addRenderableWidget(new Button(this.width / 2 - 50, this.height / 6 + 96 + 24 - 6, 100, 20,
                new TextComponent("Nya?"), e -> onPress("Nyaaa~")));
    }

    private void onPress(String buttonLabel) {
        // Handle button press
        Minecraft.getInstance().player.sendMessage(new TextComponent("You pressed: " + buttonLabel), Minecraft.getInstance().player.getUUID());
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);

        // Set up for texture rendering
        RenderSystem.setShaderTexture(0, SOLIAH);

        // Blit the texture at desired position (with reduced size)
        blit(poseStack, this.width / 2 - 75, this.height / 6 - 25, 0, 0, 150, 225, 200, 300);

        // Render text and other components after the background
        drawCenteredString(poseStack, this.font, "Mob Talker Dialog", this.width / 2, this.height / 6 - 24, 0xFFFFFF);

        // Draw the green text box
        fill(poseStack, this.width / 2 - 100, this.height - 40, this.width / 2 + 100, this.height - 10, 0xAA004400);
        drawCenteredString(poseStack, this.font, "Thinking about some things...", this.width / 2, this.height - 30 , 0xFFFFFF);

        // Render buttons after the anime girl texture and green text box
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause the game
    }

    @Override
    public void renderBackground(PoseStack poseStack) {
        // Disable background darkening
    }
}

