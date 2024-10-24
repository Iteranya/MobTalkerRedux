package org.arsparadox.mobtalkerredux;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class DebugTile extends Item {

    //This Guy Will Access Any Of The Files In Resources

    public DebugTile() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!world.isClientSide) { // Only run on server side
            context.getPlayer().sendMessage(new TextComponent("Hewwo World"),context.getPlayer().getUUID());
        }
        else {
            ScriptManager scriptManager = new ScriptManager();
            DialogueManager dialogue = scriptManager.loadDialogue("debug.dialogue.lua");
            Minecraft.getInstance().execute(() -> {
                        Minecraft.getInstance().setScreen(new DialogueScreen(dialogue));
                    }
            );
            WaifuManager waifuManager = new WaifuManager(context.getPlayer());

        }
        return InteractionResult.SUCCESS;
    }




}
