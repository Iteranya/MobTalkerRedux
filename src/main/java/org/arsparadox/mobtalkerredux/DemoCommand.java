package org.arsparadox.mobtalkerredux;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.arsparadox.mobtalkerredux.vn.controller.VisualNovelEngine;
import org.arsparadox.mobtalkerredux.vn.model.ScriptLoader;
import org.arsparadox.mobtalkerredux.vn.view.DialogueScreen;

import java.io.IOException;

public class DemoCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("mob_talker")
                // Basic command: /demo
                .executes(context -> {
                    if (context.getSource().getEntity() instanceof ServerPlayer player) {
                        Minecraft.getInstance().execute(() -> {
                                    try {
                                        Minecraft.getInstance().setScreen(new DialogueScreen(new VisualNovelEngine(ScriptLoader.loadDemo())));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                        );
                    }
                    return 1;
                })
                // Command with argument: /demo <name>
                .then(Commands.argument("scriptFile", StringArgumentType.word())
                        .executes(context -> {
                            String name = StringArgumentType.getString(context, "scriptFile");
                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                Minecraft.getInstance().execute(() -> {
                                            try {
                                                player.sendSystemMessage(Component.literal("Trying to load the file config/mobtalkerredux/"+name));
                                                Minecraft.getInstance().setScreen(new DialogueScreen(new VisualNovelEngine(ScriptLoader.loadScript(name))));

                                            } catch (IOException e) {
                                                player.sendSystemMessage(Component.literal("Failed to find the file config/mobtalkerredux/"+name));
                                                throw new RuntimeException(e);

                                            }
                                        }
                                );
                            }
                            return 1;
                        }))
        );
    }


}