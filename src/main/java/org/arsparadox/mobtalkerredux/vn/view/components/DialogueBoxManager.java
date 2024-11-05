package org.arsparadox.mobtalkerredux.vn.view.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class DialogueBoxManager {

    public static GuiGraphics processGui(GuiGraphics poseStack, int width,int height,String content, String label, Font font){
        int CHARACTER_NAME_OFFSET = 40;
        int DISPLAYED_SPRITE_HEIGHT = 300;
        int dialogueBoxHeight = 80;
        int DIALOGUE_BOX_PADDING = 15;
        if (content != null && !content.isEmpty()) {
            // Set dialogue box dimensions and position
            int boxWidth = Math.min(600, width - 40); // Max width of 600 or screen width - 40
            int boxX = (width - boxWidth) / 2;
            int boxY = height - dialogueBoxHeight - 5; // 20 pixels from bottom
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
            int labelWidth = font.width(label);
            int labelX = labelBoxX + (labelBoxWidth - labelWidth) / 2; // Center the label horizontally
            int labelY = labelBoxY + (labelBoxHeight - font.lineHeight) / 2; // Center the label vertically
            poseStack.drawString(font, label, labelX, labelY, 0xFFFFFF); // White text color

            // Render dialogue text with word wrapping
            List<String> wrappedText = wrapText(content, boxWidth - (DIALOGUE_BOX_PADDING * 2), font);
            int textY = boxY + DIALOGUE_BOX_PADDING;

            for (String line : wrappedText) {
                poseStack.drawString(font, line, boxX + DIALOGUE_BOX_PADDING, textY, 0xFFFFFF); // White text color
                textY += font.lineHeight + 2;
            }
        }

        if(label!=null && !label.isEmpty()){
            // Add a dark background behind the name for better readability
            int nameWidth = font.width(label);
            int nameX = (width - nameWidth) / 2;
            int nameY = (height - DISPLAYED_SPRITE_HEIGHT) / 3 - CHARACTER_NAME_OFFSET;

            // Draw name background
            poseStack.fill(
                    nameX - DIALOGUE_BOX_PADDING,
                    nameY - 2,
                    nameX + nameWidth + DIALOGUE_BOX_PADDING,
                    nameY + font.lineHeight + 2,
                    0x88000000
            );

            // Draw name
            poseStack.drawCenteredString(
                    font,
                    label,
                    width / 2,
                    nameY,
                    0xFFFFFF
            );
        }

        return poseStack;
    }


    private static List<String> wrapText(String text, int maxWidth, Font font) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (font.width(currentLine + " " + word) <= maxWidth) {
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



}
