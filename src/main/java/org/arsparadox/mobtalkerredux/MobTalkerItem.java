package org.arsparadox.mobtalkerredux;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.arsparadox.mobtalkerredux.vn.controller.VisualNovelEngine;
import org.arsparadox.mobtalkerredux.vn.controller.vnmodules.PlayerInventoryHandler;
import org.arsparadox.mobtalkerredux.vn.model.ScriptLoader;
import org.arsparadox.mobtalkerredux.vn.view.DialogueScreen;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MobTalkerItem extends Item {

    public MobTalkerItem() {
        super(new Item.Properties());
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level world = player.level();
        try{
            // Check if the entity has a custom name
            if (target.getCustomName() != null) {
                String entityName = target.getCustomName().getString();
                boolean day = world.isDay();

                if (!world.isClientSide()) { // Only run on the server side

                } else { // Client-side: Open dialogue screen
                    Minecraft minecraft = Minecraft.getInstance();
                    minecraft.execute(() -> {

                        serverSideExecute(player, entityName+".json");

                    });
                }
                return InteractionResult.SUCCESS;
            }
        } catch (Exception ignored) {

        }


        return InteractionResult.PASS; // Return PASS if the entity doesn't have a custom name
    }

    private static void serverSideExecute(Player player, String scriptFileName) {
        //String uid = player.getName().toString();//literal{Dev}
        String uid = player.getName().getString();//Dev
        PlayerInventoryHandler inventory = new PlayerInventoryHandler(player);
        long timeOfDay = player.level().getDayTime() % 24000; // Minecraft-style day/night cycle in ticks
        boolean day = (timeOfDay >= 0 && timeOfDay < 12000);
        try {
            List<Map<String,Object>> script = ScriptLoader.loadScript(scriptFileName,uid);
            if(script!=null){
                VisualNovelEngine vnEngine = new VisualNovelEngine(script, scriptFileName, uid,day,inventory);
                sendClientMessage(player, "Trying to load the file mobtalkerredux/" + scriptFileName);
                clientSideRenderDialogueScreen(vnEngine);
            }
            else{
                sendClientMessage(player, "Failed to find the file mobtalkerredux/" + scriptFileName);
            }
        } catch (IOException e) {
            sendClientMessage(player, "Failed to find the file mobtalkerredux/" + scriptFileName);
            throw new RuntimeException(e);
        }
    }

    private static void clientSideRenderDialogueScreen(VisualNovelEngine vnEngine) {
        Minecraft.getInstance().execute(() -> {
            try {
                Minecraft.getInstance().setScreen(new DialogueScreen(vnEngine));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void sendClientMessage(Player player, String message) {
        player.sendSystemMessage(Component.literal(message));
    }
}

