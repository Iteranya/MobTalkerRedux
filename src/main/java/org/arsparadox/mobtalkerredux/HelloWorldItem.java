package org.arsparadox.mobtalkerredux;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.arsparadox.mobtalkerredux.vn.controller.VisualNovelEngine;
import org.arsparadox.mobtalkerredux.vn.controller.WaifuManager;
import org.arsparadox.mobtalkerredux.vn.model.ScriptLoader;
import org.arsparadox.mobtalkerredux.vn.view.DialogueScreen;

import java.io.FileNotFoundException;
import java.util.Objects;

public class HelloWorldItem extends Item {
    public HelloWorldItem() {
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
                    try {
                        Minecraft.getInstance().setScreen(new DialogueScreen(new VisualNovelEngine(ScriptLoader.loadScript("story.json"))));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            );

            WaifuManager waifuManager = new WaifuManager(context.getPlayer());

        }
        return InteractionResult.SUCCESS;
    }




}
