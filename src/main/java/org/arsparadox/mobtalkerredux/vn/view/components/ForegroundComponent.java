package org.arsparadox.mobtalkerredux.vn.view.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.arsparadox.mobtalkerredux.vn.data.SpriteState;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForegroundComponent {
    // MINECRAFT RENDERING SYSTEM IS A NIGHTMARE!!!
    // FUCK, I have to make this BS
    // God, I hate Math, I refuse to do math
    // AND WHY THE FUCK DO PEOPLE USE PIXELS!?
    // COME ON What's simpler than pixels???
    // Aspect Ratio, no funni number above 20. I suck at math, triple digit numbers scares me.
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
    public static GuiGraphics processForeground(GuiGraphics poseStack, int width, int height, List<SpriteState> spritesToRender, Map<String, Object> variables){

            for (SpriteState sprite : spritesToRender) {
                String spriteLocationRaw = sprite.getLocation();
                String spriteLocation = replaceTemplateVariables(variables,spriteLocationRaw);
                ResourceLocation currentSprite = new ResourceLocation(
                        "mobtalkerredux", "textures/" + spriteLocation
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

                int wBlocks = (int) (width/wRatio); //Actual Pixel Size of the Screen
                int hBlocks = (int) (height/hRatio); //Actual Pixel Size of the Screen

                int wThingBlock = (int) (wBlocks*frameWRatio);
                int hThingBlock = (int) (hBlocks*frameHRatio);

                int startColumnPos = (int) (wBlocks*(startColumn-1));
                int startRowPos = (int) (hBlocks*(startRow-1)); // Immediately regretted my decision there...

                // Fuck, these aren't squares aren't they? Shit...
                // Screw it, we'll see how this'll look like, then complain
                // Okay, those are positioning, the frame size... Next up is... Image Dimensions

                // Now how do we 'Fit' this fucker???

                poseStack.blit(
                        currentSprite, // The Thing
                        startColumnPos, // The x location, I think it's the
                        startRowPos, // The y location
                        0,  // source x I don't know what this does...
                        0,  // source y Oh nyooooo~
                        wThingBlock,   // What even is this?
                        hThingBlock,  // No seriously what is this???
                        wThingBlock,  // What's the difference!?
                        hThingBlock  // FUCK!!!
                );
            }

        return poseStack;
    }



    public static String replaceTemplateVariables(Map<String, Object> variables, String template) {
        if (template == null || variables == null) {
            return template;
        }

        String result = template;
        Pattern pattern = Pattern.compile("<(.*?)>");
        Matcher matcher = pattern.matcher(template);

        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = variables.getOrDefault(key, "default");

            result = result.replace("<" + key + ">", value.toString());
        }

        return result;
    }



}
