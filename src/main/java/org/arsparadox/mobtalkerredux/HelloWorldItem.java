package org.arsparadox.mobtalkerredux;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.arsparadox.mobtalkerredux.vn.controller.ScriptManager;
import org.arsparadox.mobtalkerredux.vn.controller.WaifuManager;

import java.io.FileNotFoundException;

public class HelloWorldItem extends Item {
    public HelloWorldItem() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!world.isClientSide) { // Only run on server side
            context.getPlayer().sendMessage(new TextComponent("Hewwo World"),context.getPlayer().getUUID());
        }
        else {
//            ScriptLoader scriptLoader = new ScriptLoader();
//            DialogueScreenVM dialogue = scriptLoader.loadDialogue("debug.dialogue.lua");
            try {
                ScriptManager scriptManager = new ScriptManager("story.json");
                Minecraft.getInstance().execute(() -> {
                            Minecraft.getInstance().setScreen(scriptManager.dialogueScreen);
                        }
                );
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            WaifuManager waifuManager = new WaifuManager(context.getPlayer());

        }
        return InteractionResult.SUCCESS;
    }




}
