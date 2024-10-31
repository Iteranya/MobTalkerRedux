package org.arsparadox.mobtalkerredux.vn.controller;

import com.mojang.blaze3d.vertex.PoseStack;
import org.arsparadox.mobtalkerredux.vn.model.ScriptLoader;
import org.arsparadox.mobtalkerredux.vn.view.DialogueScreen;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public class ScriptManager {

    public DialogueScreen dialogueScreen;

    List<Map<String, Object>> vn_data;

    VisualNovelEngine visualNovelEngine;

    public ScriptManager(String scriptPath) throws FileNotFoundException {

        this.vn_data = ScriptLoader.loadScript(scriptPath);
        this.dialogueScreen = new DialogueScreen(this);
        this.visualNovelEngine = new VisualNovelEngine(vn_data);

    }

    public void leftClick(){
        visualNovelEngine.isEngineRunning = true;
    }

    public void buttonPress(String label){
        visualNovelEngine.changeStateByLabel(label);
    }


    public void update(PoseStack poseStack) {
        visualNovelEngine.startReadingScript(this.dialogueScreen,poseStack);
    }
}
