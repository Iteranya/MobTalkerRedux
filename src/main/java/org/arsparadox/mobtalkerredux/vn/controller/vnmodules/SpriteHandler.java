package org.arsparadox.mobtalkerredux.vn.controller.vnmodules;

import org.arsparadox.mobtalkerredux.vn.controller.VisualNovelEngine;
import org.arsparadox.mobtalkerredux.vn.data.DialogueState;
import org.arsparadox.mobtalkerredux.vn.data.SpriteState;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SpriteHandler {

    public static void removeSprite(String remove, DialogueState state){
        removeSpriteByFolder(state.getSprites(), remove);
    }

    public static void updateSprite(Map<String, Object> sprite, VisualNovelEngine vn) {
        String spritePos;
        if(sprite.get("position")==null){
            spritePos = "CUSTOM";
        }
        else{
            spritePos = (String) sprite.get("position");
        }
        SpriteState newSprite;
        newSprite = (new SpriteState(
                (String) sprite.get("sprite"),
                (String) sprite.get("location"),
                spritePos
        ));

        if(Objects.equals((String) sprite.get("action"), "show")){
            if(sprite.get("wRatio")!=null){
                newSprite.setPositioning(
                        ((Long) sprite.get("wRatio")).intValue(),
                        ((Long) sprite.get("hRatio")).intValue(),
                        ((Long) sprite.get("wFrameRatio")).intValue(),
                        ((Long) sprite.get("hFrameRatio")).intValue(),
                        ((Long) sprite.get("column")).intValue(),
                        ((Long) sprite.get("row")).intValue()
                );
            }
            for (SpriteState oldSprite: vn.state.getSprites()) {

                if(Objects.equals(oldSprite.getSprite(), newSprite.getSprite())){
                    removeSpriteByFolder(vn.state.getSprites(), newSprite.getSprite());
                    break;
                }
            }
            //System.out.println("Adding New Sprite: " + newSprite.getSprite());
            vn.state.addSprite(newSprite);
        }
        vn.currentState.incrementAndGet();
    }
    public static void removeSpriteByFolder(List<SpriteState> sprites, String folderName) {
        sprites.removeIf(sprite -> sprite.getSprite().equals(folderName));
    }
}
