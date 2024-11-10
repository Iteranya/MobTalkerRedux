package org.arsparadox.mobtalkerredux.vn.controller.vnmodules;

import org.arsparadox.mobtalkerredux.vn.data.DialogueState;
import org.arsparadox.mobtalkerredux.vn.data.SpriteState;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class SpriteHandler {

    public static void removeSprite(String remove, DialogueState state){
        //System.out.println("Try to remove sprite: "+ remove);
        removeSpriteByFolder(state.getSprites(), remove,state);
    }

    public static void updateSprite(Map<String, Object> sprite, DialogueState state, AtomicLong currentState) {
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
            for (SpriteState oldSprite: state.getSprites()) {

                if(Objects.equals(oldSprite.getSprite(), newSprite.getSprite())){
                    removeSpriteByFolder(state.getSprites(), newSprite.getSprite(),state);
                    break;
                }
            }
            //System.out.println("Adding New Sprite: " + newSprite.getSprite());
            state.addSprite(newSprite);
        }
        currentState.incrementAndGet();
    }
    public static void removeSpriteByFolder(List<SpriteState> sprites, String folderName,DialogueState state) {
        sprites.removeIf(sprite -> sprite.getSprite().equals(folderName));
    }
}
