package org.arsparadox.mobtalkerredux;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class DebugTile extends Item {

    //This Guy Will Access Any Of The Files In Resources

    public DebugTile() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!world.isClientSide) { // Only run on server side
            Objects.requireNonNull(context.getPlayer()).sendSystemMessage(
                    Component.literal("Hewwo World~")
            );
        }
        else {
//            ScriptLoader scriptLoader = new ScriptLoader();
//            DialogueScreenVM dialogue = scriptLoader.loadDialogue("debug.dialogue.lua");
            Minecraft.getInstance().execute(() -> {
//                        Minecraft.getInstance().setScreen(new DialogueScreen(dialogue));
                    }
            );
        }
        return InteractionResult.SUCCESS;
    }




}
