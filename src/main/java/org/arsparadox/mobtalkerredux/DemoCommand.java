package org.arsparadox.mobtalkerredux;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.arsparadox.mobtalkerredux.vn.controller.vnmodules.PlayerInventoryHandler;
import org.arsparadox.mobtalkerredux.vn.controller.VisualNovelEngine;
import org.arsparadox.mobtalkerredux.vn.model.ScriptLoader;
import org.arsparadox.mobtalkerredux.vn.view.DialogueScreen;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DemoCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("mob_talker")
                .executes(context -> {
                    if (context.getSource().getEntity() instanceof ServerPlayer player) {
                        serverSideExecute(player, "demo.json");
                    }
                    return 1;
                })
                .then(Commands.argument("scriptFile", StringArgumentType.word())
                        .executes(context -> {
                            String scriptFileName = StringArgumentType.getString(context, "scriptFile");
                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                serverSideExecute(player, scriptFileName);
                            }
                            return 1;
                        }))
        );
    }

    private static void serverSideExecute(ServerPlayer player, String scriptFileName) {
        String uid = player.getName().getString();
        PlayerInventoryHandler inventory = new PlayerInventoryHandler(player);
        boolean day = player.level().isDay();

        try {
            List<Map<String,Object>> script = ScriptLoader.loadScript(scriptFileName,uid);
            List<Map<String,Object>> save = ScriptLoader.loadSave(scriptFileName,uid);
            List<Map<String,Object>> global = ScriptLoader.loadGlobal(uid);
            VisualNovelEngine vnEngine = new VisualNovelEngine(script, scriptFileName, uid,day,inventory,global,save);
            sendClientMessage(player, "Trying to load the file config/mobtalkerredux/" + scriptFileName);
            clientSideRenderDialogueScreen(vnEngine);
        } catch (IOException e) {
            sendClientMessage(player, "Failed to find the file config/mobtalkerredux/" + scriptFileName);
            throw new RuntimeException(e);
        }
    }

    private static void clientSideRenderDialogueScreen(VisualNovelEngine vnEngine) {

        Minecraft.getInstance().execute(() -> {
            try {
                //Minecraft.getInstance().setScreen(new DialogueScreen(vnEngine,player));
                Minecraft.getInstance().setScreen(new DialogueScreen(vnEngine,null));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void sendClientMessage(ServerPlayer player, String message) {
        player.sendSystemMessage(Component.literal(message));
    }
}
