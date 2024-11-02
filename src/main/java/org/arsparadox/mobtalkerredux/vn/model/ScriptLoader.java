package org.arsparadox.mobtalkerredux.vn.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class ScriptLoader{

    public static ArrayNode loadJson(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Parse the JSON file and return it as an ArrayNode
            JsonNode jsonNode = objectMapper.readTree(new File(filePath));
            if (jsonNode.isArray()) {
                return (ArrayNode) jsonNode;
            } else {
                throw new RuntimeException("JSON is not an array at " + filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load file at " + filePath, e);
        }
    }

//    public static DialogueList loadDialogue(String filePath) throws FileNotFoundException {
//        return DialogueParser.parseDialogue(loadJson(filePath).toString());
//    }

    public static List<Map<String, Object>> loadScript(String filePath) throws FileNotFoundException {
        filePath = FMLPaths.CONFIGDIR.get() +"\\mobtalkerredux\\" +filePath;
        ArrayNode jsonArray = loadJson(filePath);
        return convertJsonArrayToList(jsonArray);
    }

    private static List<Map<String, Object>> convertJsonArrayToList(ArrayNode arrayNode) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (JsonNode element : arrayNode) {
            if (element.isObject()) {
                result.add(convertJsonObjectToMap((ObjectNode) element));
            }
        }

        return result;
    }

    private static Map<String, Object> convertJsonObjectToMap(ObjectNode objectNode) {
        Map<String, Object> map = new HashMap<>();

        Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            JsonNode value = entry.getValue();

            if (value.isNumber()) {
                map.put(entry.getKey(), value.numberValue());
            } else if (value.isTextual()) {
                map.put(entry.getKey(), value.textValue());
            } else if (value.isBoolean()) {
                map.put(entry.getKey(), value.booleanValue());
            } else if (value.isArray()) {
                map.put(entry.getKey(), convertJsonArrayToList((ArrayNode) value));
            } else if (value.isObject()) {
                map.put(entry.getKey(), convertJsonObjectToMap((ObjectNode) value));
            }
        }

        return map;
    }
}