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
import org.arsparadox.mobtalkerredux.command.TeamHandler;
import org.arsparadox.mobtalkerredux.vn.controller.VisualNovelEngine;
import org.arsparadox.mobtalkerredux.vn.controller.vnmodules.PlayerInventoryHandler;
import org.arsparadox.mobtalkerredux.vn.model.ScriptLoader;
import org.arsparadox.mobtalkerredux.vn.view.DialogueScreen;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MobTalkerItem extends Item {

    public MobTalkerItem() {
        super(new Item.Properties());
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level world = player.level();

        try {
            // Check if the entity has a custom name
            if (target.getCustomName() != null) {
                String entityName = target.getCustomName().getString().toLowerCase().replace(" ", "_");
                String entityType = target.getType().toShortString();
                if (!world.isClientSide()) {
                    // Safely check and cast to ServerPlayer
                } else { // Client-side: Open dialogue screen
                    Minecraft minecraft = Minecraft.getInstance();
                    //CustomItemUtils.giveCustomItem(player,"variant1");
                    minecraft.execute(() -> {
                        sendClientMessage(player,"The Entity's type is: "+entityType);
                        VisualNovelEngine vn = serverSideExecute(player, entityType, entityName,target);
                        if(vn!=null){
                            clientSideRenderDialogueScreen(vn,target,player);

                        }

                    });
                }
                return InteractionResult.SUCCESS;
            }
        } catch (Exception ignored) {
            System.out.println(ignored.toString());
        }

        return InteractionResult.PASS; // Return PASS if the entity doesn't have a custom name
    }

    private static VisualNovelEngine serverSideExecute(Player player, String entityType,String entityName, LivingEntity target) {
        //String uid = player.getName().toString();//literal{Dev}
        String uid = player.getName().getString();//Dev
        PlayerInventoryHandler inventory = new PlayerInventoryHandler(player);
        long timeOfDay = player.level().getDayTime() % 24000; // Minecraft-style day/night cycle in ticks
        boolean day = (timeOfDay >= 0 && timeOfDay < 12000);
        try {
            List<Map<String,Object>> script = ScriptLoader.loadScript(entityName,entityType,uid);
            List<Map<String,Object>> localSave = ScriptLoader.loadSave(entityName,uid);
            List<Map<String,Object>> globalSave = ScriptLoader.loadGlobal(uid);
            if(script!=null){
                VisualNovelEngine vnEngine = new VisualNovelEngine(
                        script,
                        entityType,
                        entityName,
                        uid,
                        day,
                        inventory,
                        globalSave,
                        localSave
                );
                TeamHandler.addToPlayerTeam(player,target);
                return vnEngine;
                // sendClientMessage(player, "Trying to load the file mobtalkerredux/" + scriptFileName);
                //clientSideRenderDialogueScreen(vnEngine,target,player);
            }
            else{
                //sendClientMessage(player, "Failed to find the file mobtalkerredux/" + scriptFileName);
            }
        } catch (IOException e) {
            //sendClientMessage(player, "Failed to find the file mobtalkerredux/" + scriptFileName);
            throw new RuntimeException(e);
        }
        return null;
    }

    private static void clientSideRenderDialogueScreen(VisualNovelEngine vnEngine, LivingEntity target,Player player) {
        Minecraft.getInstance().execute(() -> {
            try {
                Minecraft.getInstance().setScreen(new DialogueScreen(vnEngine,target,player));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void sendClientMessage(Player player, String message) {
        player.sendSystemMessage(Component.literal(message));
    }
}

