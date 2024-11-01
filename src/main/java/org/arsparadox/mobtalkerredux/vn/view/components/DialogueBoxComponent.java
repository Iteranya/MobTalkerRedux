//package org.arsparadox.mobtalkerredux.vn.view.components;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import net.minecraft.client.gui.Font;
//import net.minecraft.client.gui.GuiComponent;
//
//import javax.annotation.Nullable;
//import java.util.ArrayList;
//import java.util.List;
//
//public class DialogueBoxComponent extends GuiComponent {
//    private static final int DIALOGUE_BOX_PADDING = 10;
//    private String content;
//
//    private int dialogueBoxHeight;
//
//
//
//    public DialogueBoxComponent()  {
//        this.dialogueBoxHeight = 80;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }
//
//    public void render(PoseStack poseStack) {
//        if (content != null) {
//            // Calculate dialogue box dimensions
//            int boxWidth = Math.min(600, width - 40);
//            int boxX = (width - boxWidth) / 2;
//            int boxY = height - dialogueBoxHeight - 20;
//
//            // Draw dialogue box background with gradient
//            fill(poseStack, boxX, boxY, boxX + boxWidth, boxY + dialogueBoxHeight, 0xCC000000);
//
//            // Add border
//            fill(poseStack, boxX - 1, boxY - 1, boxX + boxWidth + 1, boxY + dialogueBoxHeight + 1, 0xFF004400);
//
//            // Word wrap and render dialogue text
//            List<String> wrappedText = wrapText(content, boxWidth - (DIALOGUE_BOX_PADDING * 2));
//            int textY = boxY + DIALOGUE_BOX_PADDING;
//
//            for (String line : wrappedText) {
//                drawString(poseStack, font, line, boxX + DIALOGUE_BOX_PADDING, textY, 0xFFFFFF);
//                textY += font.lineHeight + 2;
//            }
//        }
//    }
//
//    private List<String> wrapText(String text, int maxWidth) {
//        List<String> lines = new ArrayList<>();
//        String[] words = text.split(" ");
//        StringBuilder currentLine = new StringBuilder();
//
//        for (String word : words) {
//            if (this.font.width(currentLine + " " + word) <= maxWidth) {
//                if (currentLine.length() > 0) currentLine.append(" ");
//                currentLine.append(word);
//            } else {
//                if (currentLine.length() > 0) {
//                    lines.add(currentLine.toString());
//                    currentLine = new StringBuilder();
//                }
//                currentLine.append(word);
//            }
//        }
//
//        if (currentLine.length() > 0) {
//            lines.add(currentLine.toString());
//        }
//
//        return lines;
//    }
//
//
//}
