package org.arsparadox.mobtalkerredux.lua;

import net.minecraftforge.fml.loading.FMLPaths;
import org.arsparadox.mobtalkerredux.DialogueManager;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LuaBridge {
    private final DialogueManager dialogueManager;
    private final Globals globals;
    private volatile boolean isRunning;
    private LuaValue currentSceneFunction;

    public LuaBridge() {
        this.dialogueManager = new DialogueManager();
        this.globals = JsePlatform.standardGlobals();
        this.isRunning = true;

        setupLuaFunctions();
        loadInitialScript();
        startInitialScene();
    }

    private void setupLuaFunctions() {
        // Setup the 'say' function
        globals.set("say", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue message) {
                if (!isRunning) return LuaValue.NONE;
                System.out.println("Queueing "+ message.checkjstring());
                dialogueManager.queueDialogue(message.checkjstring());
                return LuaValue.NONE;
            }
        });

        // Setup the 'show' function for character names/sprites
        globals.set("show", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue character) {
                if (!isRunning) return LuaValue.NONE;
                dialogueManager.queueCharacterName(character.checkjstring());
                return LuaValue.NONE;
            }
        });

        // Setup the 'choices' function
        globals.set("choices", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue options) {
                if (!isRunning) return LuaValue.NONE;

                LuaTable table = options.checktable();
                Map<String, String> choicesMap = new HashMap<>();

                // Process each choice option
                for (int i = 1; i <= table.length(); i++) {
                    LuaTable choice = table.get(i).checktable();
                    String text = choice.get("text").checkjstring();
                    String nextScene = choice.get("nextScene").checkjstring();

                    choicesMap.put(String.valueOf(i), text);
                    dialogueManager.addChoiceMapping(String.valueOf(i), nextScene);
                }

                dialogueManager.queueChoices(choicesMap);
                return LuaValue.NONE;
            }
        });

        // Setup the 'endVN' function
        globals.set("endVN", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue message) {
                if (!isRunning) return LuaValue.NONE;
                dialogueManager.queueDialogue(message.checkjstring());
                stop();
                return LuaValue.NONE;
            }
        });
    }

    private void loadInitialScript() {
        try {
            Path configPath = FMLPaths.CONFIGDIR.get();
            String scriptPath = configPath.toString() + "/mobtalkerredux/characters/cupa/debug.function.lua";
            globals.loadfile(scriptPath).call();
            dialogueManager.setGlobals(globals);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load Lua script: " + e.getMessage());
            stop();
        }
    }

//    private void startInitialScene() {
//        try {
//            String initialScene = globals.get("currentScene").checkjstring();
//            currentSceneFunction = globals.get("scenes").get(initialScene).checktable();
//
//            // Execute the scene function to queue up all dialogue
//            currentSceneFunction.call();
//
//            // Display the first dialogue entry
//            dialogueManager.displayNext();
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Failed to start initial scene: " + e.getMessage());
//            stop();
//        }
//    }

    private void startInitialScene() {
        try {
            String initialScene = globals.get("currentScene").checkjstring();
            LuaValue scenes = globals.get("scenes");
            currentSceneFunction = scenes.get(initialScene);  // Get the function directly
            System.out.println("Initial Screen: " + initialScene);
            // Execute the scene function to queue up all dialogue
            if (!currentSceneFunction.isfunction()) {
                throw new IllegalStateException("Scene '" + initialScene + "' is not a function");
            }
//            currentSceneFunction.call();  // Call the function directly

            // Display the first dialogue entry
            dialogueManager.displayNext();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to start initial scene: " + e.getMessage());
            stop();
        }
    }

    // Method to handle scene transitions
//    public void transitionToScene(String sceneName) {
//        if (!isRunning) return;
//
//        try {
//            dialogueManager.clearAll();
//            globals.set("currentScene", LuaValue.valueOf(sceneName));
//            currentSceneFunction = globals.get("scenes").get(sceneName).checktable();
//            currentSceneFunction.call();
//            dialogueManager.displayNext();
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Failed to transition to scene " + sceneName + ": " + e.getMessage());
//        }
//    }

    public void transitionToScene(String sceneName) {
        if (!isRunning) return;

        try {
            dialogueManager.clearAll();
            globals.set("currentScene", LuaValue.valueOf(sceneName));
            LuaValue scenes = globals.get("scenes");
            currentSceneFunction = scenes.get(sceneName);  // Get the function directly

            if (!currentSceneFunction.isfunction()) {
                throw new IllegalStateException("Scene '" + sceneName + "' is not a function");
            }
            currentSceneFunction.call();  // Call the function directly

            dialogueManager.displayNext();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to transition to scene " + sceneName + ": " + e.getMessage());
        }
    }

    // Public methods for controlling the VN
    public void stop() {
        isRunning = false;
        dialogueManager.clearAll();
    }

    public void resume() {
        isRunning = true;
    }

    public boolean isRunning() {
        return isRunning;
    }

    // Getter for DialogueManager
    public DialogueManager getDialogueManager() {
        return dialogueManager;
    }

    // Method to advance dialogue
    public void advance() {
        if (!isRunning) return;
        dialogueManager.displayNext();
    }
}