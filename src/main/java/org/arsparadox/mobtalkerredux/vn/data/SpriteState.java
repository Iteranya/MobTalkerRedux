package org.arsparadox.mobtalkerredux.vn.data;

import java.util.ArrayList;
import java.util.List;

public class SpriteState {
    private String name;
    private String type;
    private String folder;

    private List<String> versions = new ArrayList<>();

    public SpriteState(String name, String type, String folder) {
        this.name = name;
        this.type = type;
        this.folder = folder;
    }
//
//    // Getters and setters
//    public String getLabel() { return label; }
//    public void setLabel(String label) { this.label = label; }
//    public String getContent() { return content; }
//    public void setContent(String content) { this.content = content; }
//    public String getSprite() { return sprite; }
//    public void setSprite(String sprite) { this.sprite = sprite; }
//
//    public List<Map<String, Object>> getChoices() {
//        return choices;
//    }
//    public void setChoices(List<Map<String, Object>> choices) { this.choices = choices; }

}
