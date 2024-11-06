package org.arsparadox.mobtalkerredux.vn.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.arsparadox.mobtalkerredux.MobTalkerRedux;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private static List<Map<String, Object>> loadJsonFromFile(String filePath) {
        try {
            String jsonContent = Files.readString(Path.of(filePath));
            Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            return gson.fromJson(jsonContent, listType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load file at " + filePath, e);
        }
    }

    private static List<Map<String, Object>> loadJsonFromStream(InputStream content) {
        try (Reader reader = new InputStreamReader(content)) {
            Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load file from stream", e);
        }
    }

    public static List<Map<String, Object>> loadFromConfig(String fileName) {
        String filePath = getConfigFilePath(fileName);
        return loadJsonFromFile(filePath);
    }

    public static List<Map<String, Object>> loadFromResource(String resourceName) throws IOException {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        ResourceLocation resourceLocation = new ResourceLocation(MobTalkerRedux.MODID, resourceName);

        Resource resource = resourceManager.getResource(resourceLocation).orElseThrow(
                () -> new FileNotFoundException("Resource file " + resourceName + " could not be found.")
        );

        try (InputStream inputStream = resource.open()) {
            return loadJsonFromStream(inputStream);
        }
    }

    public static String getWorldName() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            return server.getWorldData().getLevelName();
        }
        throw new RuntimeException("Could not get the world name, server is not running!");
    }


    /**
     * Loads JSON data from the save folder, config directory, or resources in that order.
     * @param fileName Name of the JSON file.
     * @param playerUID Player-specific UID if applicable.
     * @return Deserialized list of maps representing the JSON data.
     * @throws IOException if the file is not found in any of the locations.
     */
    public static List<Map<String, Object>> loadScript(String fileName, String playerUID) throws IOException {
        // Try loading from the save folder (level or player UID folder)
        File saveFile = new File(getSaveFilePath(fileName, playerUID,getWorldName()));
        if (saveFile.exists()) {
            System.out.println("Loading from save folder: " + fileName);
            return loadJsonFromFile(saveFile.getPath());
        }

        // If not found in save, try loading from config
        File configFile = new File(getConfigFilePath(fileName));
        if (configFile.exists()) {
            System.out.println("Loading from config directory: " + fileName);
            return loadJsonFromFile(configFile.getPath());
        }

        // If not found in save or config, try loading from resources
        System.out.println("Loading from resources: " + fileName);
        return loadFromResource(fileName);
    }

    /**
     * Saves the current game state to a JSON file in the save folder.
     * @param gameState The data to save.
     * @param fileName Name of the JSON file to save.
     * @param playerName Player-specific UID for saving to a specific folder.
     */
    public static void saveState(List<Map<String, Object>> gameState, String fileName, String playerName) {
        String filePath = getSaveFilePath(fileName, playerName,getWorldName());
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(gameState, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save game state to " + filePath, e);
        }
    }

    /**
     * Constructs the path to a file in the configuration directory.
     * @param fileName Name of the file.
     * @return Absolute path to the config file.
     */
    private static String getConfigFilePath(String fileName) {
        return FMLPaths.CONFIGDIR.get() + File.separator + MobTalkerRedux.MODID + File.separator + fileName;
    }

    /**
     * Constructs the path to a save file in the level save folder or player UID-specific folder.
     * @param fileName Name of the file.
     * @param playerName The unique player ID, used to generate a player-specific save path.
     * @param levelName The unique player ID, used to generate a player-specific save path.
     * @return Absolute path to the save file.
     */
    private static String getSaveFilePath(String fileName, String playerName, String levelName) {
        String saveDir = Minecraft.getInstance().gameDirectory.getAbsolutePath() + File.separator + "saves" + File.separator +levelName+File.separator+ playerName;
        new File(saveDir).mkdirs(); // Ensure the directory exists
        return saveDir + File.separator + fileName;
    }
}
