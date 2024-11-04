package org.arsparadox.mobtalkerredux.vn.data;

public class SpriteState {
    private String folder;
    private String position;
    private String location;

    int wRatio = 16; // These guys
    int hRatio = 9; // Will Default
    int frameWRatio =5; // To putting image
    int frameHRatio = 8; // In the middle
    int startColumn = 7; // Of the screen
    int startRow = 1; //First row is one, we don't do zero, this isn't an array

    boolean animationEnabled = false;
    boolean animationExists = false;

    int wRatioAnimation = 16; // These guys
    int hRatioAnimation = 9; // Will Default
    int frameWRatioAnimation =5; // To putting image
    int frameHRatioAnimation = 8; // In the middle
    int startColumnAnimation = 7; // Of the screen
    int startRowAnimation = 1; //First row is one, we don't do zero, this isn't an array

    public SpriteState(String folder,String location,String position) {
        this.folder = folder;
        this.location = location;
        this.position = position;
    }

    // Getters and setters

    public boolean getAnimationStatus(){return animationEnabled;}
    public void disableAnimation(){this.animationEnabled = false;}
    public void enableAnimation(){this.animationEnabled = true;}

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getSprite() { return folder; }
    public void setSprite(String sprite) { this.folder = sprite; }

    public double getwRatio(){return wRatio;}
    public double gethRatio(){return hRatio;}
    public double getFrameWRatio(){return frameWRatio;}
    public double getFrameHRatio(){return frameHRatio;}
    public double getStartColumn(){return startColumn;}
    public double getStartRow(){return startRow;}

    public void setPositioning(double wRatio, double hRatio, double frameWRatio, double frameHRatio, double column, double row){
        this.wRatio = (int) wRatio;
        this.hRatio = (int) hRatio;
        this.frameWRatio = (int) frameWRatio;
        this.frameHRatio = (int) frameHRatio;
        this.startColumn = (int) column;
        this.startRow = (int) row;
    }



    public String getPosition(){
        if(position==null)return "CENTER";
        else return position;
    }
    public void setPosition(String position){this.position = position;}

}
