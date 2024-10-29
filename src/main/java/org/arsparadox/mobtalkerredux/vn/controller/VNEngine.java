package org.arsparadox.mobtalkerredux.vn.controller;

import org.arsparadox.mobtalkerredux.vn.data.dialogue.Action;
import org.arsparadox.mobtalkerredux.vn.data.dialogue.State;
import org.arsparadox.mobtalkerredux.vn.model.ScriptLoader;
import org.arsparadox.mobtalkerredux.vn.view.DialogueScreenVM;

import javax.json.JsonObject;
import java.io.FileNotFoundException;

public class VNEngine {
    ScriptLoader scriptLoader = new ScriptLoader();
    String jsonPath;

    JsonObject json = scriptLoader.loadJson(jsonPath);
    // Access current state

    VisualNovelFSM fsm = VisualNovelFSM.fromJson(json);

    State currentState = fsm.states().get(fsm.initialState());

    public VNEngine(String jsonPath) throws FileNotFoundException {
        this.jsonPath = jsonPath;
    }

    private void execute(){
        for(Action action : currentState.actions()) {
            switch (action) {
                case Action.DialogueAction d -> handleDialogue(d.label(), d.content());
                case Action.ShowSpriteAction s -> showSprite(s.sprite());
            }
        }
    }


}
