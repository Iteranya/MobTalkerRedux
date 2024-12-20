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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        try{
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            ResourceLocation resourceLocation = new ResourceLocation(MobTalkerRedux.MODID, resourceName);

            Resource resource = resourceManager.getResource(resourceLocation).orElseThrow(
                    () -> new FileNotFoundException("Resource file " + resourceName + " could not be found.")
            );

            try (InputStream inputStream = resource.open()) {
                return loadJsonFromStream(inputStream);
            }

        } catch (IOException ignored) {

        }

        return null;
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

     * @param playerUID Player-specific UID if applicable.
     * @return Deserialized list of maps representing the JSON data.
     * @throws IOException if the file is not found in any of the locations.
     */
    public static List<Map<String, Object>> loadScript(String name,String type, String playerUID) throws IOException {
        // Try loading from the save folder (level or player UID folder)
        name = name.toLowerCase()+".json";
        type = type.toLowerCase()+".json";

        // If not found in save or config, try loading from resources
        System.out.println("Loading from resources: " + name);
        List<Map<String, Object>> script = loadFromResource(name);
        if(script!=null){
            return script;
        }else{
            return loadFromResource(type);
        }

    }

    public static List<Map<String, Object>> loadSave(String entityName, String playerUID) {
        // Try loading from the save folder (level or player UID folder)
        entityName = entityName.toLowerCase()+".json";
        File saveFile = new File(getSaveFilePath(entityName, playerUID,getWorldName()));
        if (saveFile.exists()) {
            System.out.println("Loading from save folder: " + entityName);
            return loadJsonFromFile(saveFile.getPath());
        }
        return null;
    }

    public static List<Map<String, Object>> loadGlobal(String playerUID) {
        // Try loading from the save folder (level or player UID folder)
        String fileName = "global.json";
        File saveFile = new File(getSaveFilePath(fileName, playerUID,getWorldName()));
        if (saveFile.exists()) {
            System.out.println("Loading from save folder: " + fileName);
            return loadJsonFromFile(saveFile.getPath());
        }
        return null;
    }

    /**
     * Saves the current game state to a JSON file in the save folder.
     * @param gameState The data to save.
     * @param fileName Name of the JSON file to save.
     * @param playerName Player-specific UID for saving to a specific folder.
     */
    public static void saveState(List<Map<String, Object>> gameState, String fileName, String playerName) {
        fileName = fileName.toLowerCase()+".json";
        playerName = playerName.toLowerCase();
        String filePath = getSaveFilePath(fileName, playerName,getWorldName());
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(gameState, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save game state to " + filePath, e);
        }
    }

    public static void saveGlobal(List<Map<String, Object>> save, String playerName) {
        playerName = playerName.toLowerCase();
        String filePath = getSaveFilePath("global.json", playerName,getWorldName());
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(save, writer);
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
        fileName = fileName.toLowerCase();
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
        fileName = fileName.toLowerCase();
        playerName = playerName.toLowerCase();
        levelName = levelName.toLowerCase();
        String saveDir = Minecraft.getInstance().gameDirectory.getAbsolutePath() + File.separator + "saves" + File.separator +levelName+File.separator+ playerName;
        new File(saveDir).mkdirs(); // Ensure the directory exists
        return saveDir + File.separator + fileName;
    }

    public static void loadTextureFromConfig() {
        Path configTexturePath = Paths.get(FMLPaths.CONFIGDIR.get().toString(), MobTalkerRedux.MODID, "textures");
        Path modAssetsPath = Paths.get("assets", "mobtalkerredux", "textures");

        // Check if the file exists in config
        if (Files.exists(configTexturePath)) {
            try {
                // Read the image from config
                BufferedImage image = ImageIO.read(configTexturePath.toFile());

                // Ensure the target directory exists
                Files.createDirectories(modAssetsPath.getParent());

                // Write the image to mod assets
                ImageIO.write(image, "png", modAssetsPath.toFile());

                System.out.println("Successfully loaded texture from config");

            } catch (IOException e) {
                System.out.println("Failed to load texture from config");
            }
        } else {
            System.out.println("No custom texture found in config");
        }
    }
}
