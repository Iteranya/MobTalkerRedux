package org.arsparadox.mobtalkerredux.vn.controller.vnmodules;

import org.arsparadox.mobtalkerredux.vn.controller.VisualNovelEngine;
import org.arsparadox.mobtalkerredux.vn.model.ScriptLoader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class SaveHandler {


    public static void processFinishing(VisualNovelEngine vn) {
        vn.isEngineRunning.set(false);
        saveProgress(vn);
        vn.shutdown.set(true);
    }

    public static String getCurrentDateTime() {
        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();

        // Format the date and time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    public static void loadProgress(VisualNovelEngine vn){
        if (vn.localSave == null || vn.localSave.isEmpty()) {
            vn.localSave = new ArrayList<>();
            vn.localVariables = new HashMap<>();
            vn.localVariables.put("type", "variable");
            vn.localSave.add(new HashMap<>(vn.localVariables));
            System.out.println("Initialize Local Variable");
        } else {
            vn.localVariables.clear();
            vn.localVariables.putAll(vn.localSave.get(vn.localSave.size() - 1));

            if (vn.localVariables.get("checkpoint") != null &&
                    !((String) vn.localVariables.get("checkpoint")).isEmpty()) {
                vn.currentState.set(
                        StateHandler.findLabelId((String) vn.localVariables.get("checkpoint"), vn.gameData)
                );
            }
        }
        if (vn.globalSave == null || vn.globalSave.isEmpty()) {
            vn.globalSave = new ArrayList<>();
            vn.globalVariables = new HashMap<>(); // brand new map
            vn.globalVariables.put("type", "variable");
            vn.globalSave.add(new HashMap<>(vn.globalVariables));
            System.out.println("Initialize Global Variable");
        } else {
            vn.globalVariables.clear();

            vn.globalVariables.putAll(vn.globalSave.get(vn.globalSave.size() - 1));
            System.out.println("Input global variable: "+ vn.globalVariables);
        }
    }

    public static void saveProgress(VisualNovelEngine vn){
        vn.localVariables.put("time", getCurrentDateTime());
        vn.globalVariables.put("time",getCurrentDateTime());
        vn.globalSave.add(new HashMap<>(vn.globalVariables));
        vn.localSave.add(new HashMap<>(vn.localVariables));
        ScriptLoader.saveGlobal(vn.globalSave,vn.uid.toString());
        ScriptLoader.saveState(vn.localSave,vn.entityName.toString(),vn.uid.toString());
    }
}
