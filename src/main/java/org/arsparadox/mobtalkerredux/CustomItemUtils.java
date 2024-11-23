package org.arsparadox.mobtalkerredux;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CustomItemUtils {
    // Basic version - just give a variant
    public static void giveCustomItem(Player player, String variantName) {
        ItemStack itemStack = new ItemStack(MobTalkerRedux.RegistryEvents.CUSTOM_ITEM.get());
        CompoundTag tag = new CompoundTag();
        tag.putString("CustomName", "item.mobtalkerredux." + variantName);
        tag.putString("TextureKey", "custom_item_" + variantName);
        tag.putInt("CustomModelData", getVariantId(variantName));
        itemStack.setTag(tag);

        giveItemToPlayer(player, itemStack);
    }


    // Private helper methods
    private static void giveItemToPlayer(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            // If inventory is full, spawn item in world
            player.drop(stack, false);
        }
    }

    private static int getVariantId(String variantName) {
        // You can expand this method to map variant names to custom model data IDs
        return switch (variantName.toLowerCase()) {
            case "variant1" -> 1;
            case "variant2" -> 2;
            case "variant3" -> 3;
            default -> 1;
        };
    }
}