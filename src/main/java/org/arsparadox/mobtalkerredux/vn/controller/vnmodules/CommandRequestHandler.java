package org.arsparadox.mobtalkerredux.vn.controller.vnmodules;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class CommandRequestHandler {
    public static boolean handleCommandRequest(ServerPlayer player, String command) {
        MinecraftServer server = player.getServer();

        // Early return if server is null
        if (server == null) return false;

        // Check if it's single player
        boolean isSinglePlayer = server.isSingleplayer();

        if (isSinglePlayer) {
            // Single player - just run the command with full permissions
            return ForgeCommandRunner.runCommand(server, command);
        }

        // Multiplayer validation
        if (!isCommandAllowed(command)) {
            // Command not in whitelist
            player.sendSystemMessage(Component.literal("This command is not allowed!"));
            return false;
        }

        if (!hasPermission(player)) {
            // Player doesn't have permission
            player.sendSystemMessage(Component.literal("You don't have permission to use visual novel commands!"));
            return false;
        }

        // Rate limiting check
        if (isRateLimited(player)) {
            player.sendSystemMessage(Component.literal("Please wait before using another command!"));
            return false;
        }

        // If we get here, command is allowed - run it with appropriate permission level
        // Note: You might want to run with player's actual permission level instead of level 4
        return ForgeCommandRunner.runCommand(server, command);
    }

    private static boolean isCommandAllowed(String command) {
        // Example validation - you should implement your own logic
        // Maybe check against a config-defined whitelist
        // Maybe block dangerous commands like /op, /stop, etc.
        return !command.startsWith("/op") &&
                !command.startsWith("/stop") &&
                !command.startsWith("/ban");
    }

    private static boolean hasPermission(ServerPlayer player) {
        // Example permission check
        // Could check against your mod's permission system
        // Or check player's op status
        return player.hasPermissions(2); // Level 2 is typical for command blocks
    }

    private static boolean isRateLimited(ServerPlayer player) {
        // Implement rate limiting logic
        // Could track last command time per player
        // Return true if player is sending too many commands
        return false; // Simplified for example
    }
}