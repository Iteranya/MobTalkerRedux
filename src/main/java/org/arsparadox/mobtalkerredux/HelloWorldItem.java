package org.arsparadox.mobtalkerredux;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.arsparadox.mobtalkerredux.vn.controller.VisualNovelEngine;
import org.arsparadox.mobtalkerredux.vn.model.ScriptLoader;
import org.arsparadox.mobtalkerredux.vn.view.DialogueScreen;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class HelloWorldItem extends Item {
    public HelloWorldItem() {
        super(new Item.Properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        String uid = context.getPlayer().getUUID().toString();
        if (!world.isClientSide) { // Only run on server side
            Objects.requireNonNull(context.getPlayer()).sendSystemMessage(
                    Component.literal("Hewwo World~")
            );
        }
        else {

            Minecraft.getInstance().execute(() -> {
                    try {
                        Minecraft.getInstance().setScreen(new DialogueScreen(new VisualNovelEngine(ScriptLoader.loadScript("demo.json",uid),"demo.json",uid )));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    }
            );

        }
        return InteractionResult.SUCCESS;
    }




}
