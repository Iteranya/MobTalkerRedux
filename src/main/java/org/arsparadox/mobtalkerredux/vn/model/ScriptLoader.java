package org.arsparadox.mobtalkerredux.vn.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.loading.FMLPaths;
import org.arsparadox.mobtalkerredux.MobTalkerRedux;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class ScriptLoader {

    private static final Gson gson = new GsonBuilder()
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .registerTypeAdapter(Number.class, new JsonDeserializer<Number>() {
                @Override
                public Number deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                        throws JsonParseException {
                    if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
                        double value = json.getAsDouble();
                        // Check if the number is actually an integer
                        if (value == Math.floor(value) && !Double.isInfinite(value)) {
                            if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
                                return (int) value;
                            }
                            return (long) value;
                        }
                        return value;
                    }
                    return null;
                }
            })
            .create();

    public static List<Map<String, Object>> loadJson(String filePath) {
        try {
            String jsonContent = new String(Files.readAllBytes(new File(filePath).toPath()));
            Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            return gson.fromJson(jsonContent, listType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load file at " + filePath, e);
        }
    }

    public static List<Map<String, Object>> loadJson(InputStream content) {
        try (Reader reader = new InputStreamReader(content)) {
            Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load file from stream", e);
        }
    }

    public static List<Map<String, Object>> loadScript(String filePath) {
        filePath = FMLPaths.CONFIGDIR.get() + "\\"+ MobTalkerRedux.MODID +"\\" + filePath;
        return loadJson(filePath);
    }

    public static List<Map<String, Object>> loadDemo() throws IOException {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        ResourceLocation resourceLocation = new ResourceLocation("mobtalkerredux", "demo.json");

        try (InputStream inputStream = resourceManager.getResource(resourceLocation).get().open()) {
            return loadJson(inputStream);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Resource file demo.json could not be found.");
        }
    }

    public static void saveState(Map<String, Object> variables, String filePath) {
        try {
            filePath = FMLPaths.CONFIGDIR.get() + "\\"+ MobTalkerRedux.MODID +"\\" + filePath;
            // Load existing JSON content
            List<Map<String, Object>> list = loadJson(filePath);

            // Append the 'variables' map to the list
            list.add(variables);

            // Convert the updated list back to a JSON string
            String jsonContent = gson.toJson(list);

            // Save the JSON string back into the file
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8)) {
                writer.write(jsonContent);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save state to file at " + filePath, e);
        }
    }
}