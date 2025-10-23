package org.arsparadox.mobtalkerredux.vn.controller.vnmodules;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

public class ForgeCommandRunner {

    public static CommandSourceStack getServerCommandSourceStack(MinecraftServer server) {
        return server.createCommandSourceStack().withPermission(4);
    }

    public static boolean runCommand(String command) {
        // --- CHECK IF CHEATS ARE ENABLED ---
        MinecraftServer minecraftServer = Minecraft.getInstance().getSingleplayerServer();
        if (minecraftServer == null){
            System.out.println("[ForgeCommandRunner] For Some bloody reason, single player server is null");

            return false;
        }
        if (!minecraftServer.isSingleplayer()){
            System.out.println("[ForgeCommandRunner] Cheats are disabled! Command execution blocked: " + command);
            return false;
        }

        // Optional: strip leading slash, just in case
        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        CommandSourceStack sourceStack = getServerCommandSourceStack(minecraftServer);
        try {
            CommandDispatcher<CommandSourceStack> dispatcher = minecraftServer.getCommands().getDispatcher();
            dispatcher.execute(command, sourceStack);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
