package org.arsparadox.mobtalkerredux.vn.model;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraftforge.fml.loading.FMLPaths;
import org.arsparadox.mobtalkerredux.MobTalkerRedux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TextureLoader {
    private static final String PACK_NAME = "mobtalkerredux_generated";

    public static void loadTexturesFromConfig() {
        Path configTexturePath = FMLPaths.CONFIGDIR.get().resolve(MobTalkerRedux.MODID).resolve("textures");
        Path modPath = FMLPaths.GAMEDIR.get().resolve("resourcepacks").resolve(PACK_NAME);
        Path modTexturePath = modPath.resolve("assets").resolve(MobTalkerRedux.MODID).resolve("textures");

        try {
            // Create directories if they don't exist
            Files.createDirectories(modTexturePath);

            // Create pack.mcmeta
            Path mcmetaPath = modPath.resolve("pack.mcmeta");
            if (!Files.exists(mcmetaPath)) {
                String mcmeta = """
                    {
                        "pack": {
                            "description": "Generated MobTalker Redux textures",
                            "pack_format": 15
                        }
                    }""";
                Files.writeString(mcmetaPath, mcmeta);
            }

            // Copy all files from config to mod directory
            if (Files.exists(configTexturePath)) {
                Files.walk(configTexturePath)
                        .filter(Files::isRegularFile)
                        .forEach(source -> {
                            try {
                                Path relativePath = configTexturePath.relativize(source);
                                Path targetPath = modTexturePath.resolve(relativePath);

                                // Create parent directories if they don't exist
                                Files.createDirectories(targetPath.getParent());

                                // Copy file, replacing if it exists
                                Files.copy(source, targetPath, StandardCopyOption.REPLACE_EXISTING);

                                MobTalkerRedux.LOGGER.info("Copied texture: {} to {}", source, targetPath);
                            } catch (IOException e) {
                                MobTalkerRedux.LOGGER.error("Failed to copy texture: " + source, e);
                            }
                        });
                MobTalkerRedux.LOGGER.info("Finished copying textures from config to resource pack");
            }

            // Queue the resource pack to be loaded
            Minecraft.getInstance().execute(() -> {
                try {
                    PackRepository packRepo = Minecraft.getInstance().getResourcePackRepository();

                    // Force a reload of available packs
                    packRepo.reload();

                    // Try to get the pack after reloading
                    Pack pack = packRepo.getPack(PACK_NAME);

                    if (pack != null) {
                        // If the pack isn't already enabled
                        if (!packRepo.getSelectedIds().contains(pack.getId())) {
                            // Enable the pack
                            List<Pack> enabled = new ArrayList<>(packRepo.getSelectedPacks());
                            enabled.add(pack);

                            // Update selected packs
                            packRepo.setSelected(enabled.stream().map(Pack::getId).collect(Collectors.toList()));

                            // Reload resources
                            Minecraft.getInstance().reloadResourcePacks();
                            MobTalkerRedux.LOGGER.info("Successfully loaded generated resource pack");
                        }
                    } else {
                        MobTalkerRedux.LOGGER.error("Could not find generated resource pack. Path exists: " +
                                Files.exists(modPath) + ", Pack folder contents: " +
                                Files.list(FMLPaths.GAMEDIR.get().resolve("resourcepacks"))
                                        .map(Path::getFileName)
                                        .map(Object::toString)
                                        .collect(Collectors.joining(", ")));
                    }
                } catch (Exception e) {
                    MobTalkerRedux.LOGGER.error("Failed to load resource pack", e);
                }
            });

        } catch (IOException e) {
            MobTalkerRedux.LOGGER.error("Failed to initialize texture loading", e);
        }
    }
}