package org.arsparadox.mobtalkerredux.vn.model;

import org.arsparadox.mobtalkerredux.vn.data.dialogue.DialogueList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ScriptLoader{

    private static JsonArray loadJson(String filePath) throws FileNotFoundException {
        try (InputStream is = new FileInputStream(filePath);
             JsonReader reader = Json.createReader(is)) {
            // Read the JSON as a JsonObject
            return reader.readArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static DialogueList loadDialogue(String filePath) throws FileNotFoundException {
        return DialogueParser.parseDialogue(loadJson(filePath).toString());
    }
}