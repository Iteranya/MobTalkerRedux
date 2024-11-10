package org.arsparadox.mobtalkerredux.vn.controller.vnmodules;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class PlayerInventoryHandler {
    Player player;

    public PlayerInventoryHandler(Player player){
        this.player = player;
    }
    public List<Map<String, Integer>> getInventoryContents() {
        List<Map<String, Integer>> contents = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();

        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                String itemId = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString();
                int amount = stack.getCount();
                map.put(itemId,amount);
                contents.add(map);
            }
        }

        return contents;
    }

    public int removeItemFromInventory(String itemId, int amountToRemove) {
        if (amountToRemove <= 0) return 0;

        Inventory inv = player.getInventory();
        int remainingToRemove = amountToRemove;
        int totalRemoved = 0;

        // Convert itemId to actual item
        ResourceLocation itemResource = new ResourceLocation(itemId);
        Item targetItem = ForgeRegistries.ITEMS.getValue(itemResource);
        if (targetItem == null) return 0;  // Invalid item ID

        // Go through inventory and remove items
        for (int i = 0; i < inv.getContainerSize() && remainingToRemove > 0; i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty() && stack.getItem() == targetItem) {
                int stackSize = stack.getCount();
                int toRemove = Math.min(stackSize, remainingToRemove);

                stack.shrink(toRemove);
                if (stack.isEmpty()) {
                    inv.setItem(i, ItemStack.EMPTY);
                }

                remainingToRemove -= toRemove;
                totalRemoved += toRemove;
            }
        }

        return totalRemoved;
    }

    public boolean giveItemToPlayer(String itemId, int amount) {
        if (amount <= 0) return false;

        // Convert itemId to actual item
        ResourceLocation itemResource = new ResourceLocation(itemId);
        Item targetItem = ForgeRegistries.ITEMS.getValue(itemResource);
        if (targetItem == null) return false;  // Invalid item ID
        ItemStack stack = new ItemStack(targetItem, amount);

            // Try to add to existing stacks first
            boolean status = player.getInventory().add(stack);


        return status;
    }

}
