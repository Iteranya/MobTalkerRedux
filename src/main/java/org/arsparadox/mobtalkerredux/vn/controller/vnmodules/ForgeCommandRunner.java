package org.arsparadox.mobtalkerredux.vn.controller.vnmodules;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;



///
public class ForgeCommandRunner {
    public static CommandSourceStack getServerCommandSourceStack(MinecraftServer server) {
        // Creates a command source stack with the server's permissions and context
        return server.createCommandSourceStack()
                .withPermission(4);
    }

    public static boolean runCommand(MinecraftServer server, String command) {
        CommandSourceStack sourceStack = getServerCommandSourceStack(server);
        try {
            CommandDispatcher<CommandSourceStack> dispatcher = server.getCommands().getDispatcher();
            dispatcher.execute(command, sourceStack);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
