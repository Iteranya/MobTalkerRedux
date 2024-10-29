package org.arsparadox.mobtalkerredux.vn.model;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ScriptLoader{

    public JsonObject loadJson(String filePath) throws FileNotFoundException {
        try (InputStream is = new FileInputStream(filePath);
             JsonReader reader = Json.createReader(is)) {
            // Read the JSON as a JsonObject
            return reader.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}