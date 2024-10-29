package org.arsparadox.mobtalkerredux.vn.data;

// Choice class representing a choice in dialogues
class Choice {
    private String buttonText;
    private int nextStateId;

    public Choice(String buttonText, int affectionChange, int nextStateId) {
        this.buttonText = buttonText;
        this.nextStateId = nextStateId;
    }


    public String getButtonText() {
        return buttonText;
    }

    public int getNextStateId() {
        return nextStateId;
    }
}
