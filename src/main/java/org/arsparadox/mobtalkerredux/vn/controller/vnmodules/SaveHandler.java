package org.arsparadox.mobtalkerredux.vn.controller.vnmodules;

import org.arsparadox.mobtalkerredux.vn.controller.VisualNovelEngine;
import org.arsparadox.mobtalkerredux.vn.model.ScriptLoader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SaveHandler {


    public static void processFinishing(VisualNovelEngine vn) {
        vn.isEngineRunning.set(false);
//        if (!vn.gameData.get(vn.gameData.size() - 1).equals(vn.variables)) {
//            // Add New Save Data with a timestamp to the list if it's not duplicate
//            vn.variables.put("time", getCurrentDateTime());
//        }
        vn.localVariables.put("time", getCurrentDateTime());
        vn.globalSave.add(vn.localVariables);
        ScriptLoader.saveGlobal(vn.globalSave,vn.uid.toString());
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

    }

    public static void saveProgress(VisualNovelEngine vn){

    }
}
