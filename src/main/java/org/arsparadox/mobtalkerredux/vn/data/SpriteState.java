package org.arsparadox.mobtalkerredux.vn.data;

public class SpriteState {
    private String folder;

    private String location;

    public SpriteState(String folder,String location) {
        this.folder = folder;
        this.location = location;
    }
//
    // Getters and setters
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getSprite() { return folder; }
    public void setSprite(String sprite) { this.folder = sprite; }
//
//    public List<Map<String, Object>> getChoices() {
//        return choices;
//    }
//    public void setChoices(List<Map<String, Object>> choices) { this.choices = choices; }

}
