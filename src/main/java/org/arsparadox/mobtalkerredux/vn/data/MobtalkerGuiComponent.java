package org.arsparadox.mobtalkerredux.vn.data;

public class MobtalkerGuiComponent{
    // Okay, I'm gonna need this...
    // Hmmm...
    // Just realized that theoretically you can put this anywhere...

    // I guess we start off from the 6 dimensions???
    float wRatio = 16; // These guys
    float hRatio = 9; // Will Default
    float frameWRatio =4; // To putting image
    float frameHRatio = 4; // In the middle
    float startColumn = 6; // Of the screen
    float startRow = 3;
    String name;
    String texture; // Or Image to render, whatever you call it

    // That's it~

    public MobtalkerGuiComponent(String name,String texture){
        this.name = texture;
        this.texture = texture;
    }

    public void setPositioning(double wRatio, double hRatio, double frameWRatio, double frameHRatio, double column, double row) {
        this.wRatio = (int) wRatio;
        this.hRatio = (int) hRatio;
        this.frameWRatio = (int) frameWRatio;
        this.frameHRatio = (int) frameHRatio;
        this.startColumn = (int) column;
        this.startRow = (int) row;
    }

    public String getName(){
        return this.name;
    }

    public String getTexture(){
        return this.texture;
    }

    public void setTexture(String texture){
        this.texture = texture;
    }

}
