package org.arsparadox.mobtalkerredux;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class CustomItem extends Item {
    // No Like, Literally Custom Item

    public CustomItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("CustomName")) {
            return Component.translatable(stack.getTag().getString("CustomName"));
        }
        return super.getName(stack);
    }

    // Override texture retrieval
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (stack.hasTag() && stack.getTag().contains("TextureKey")) {
            tooltip.add(Component.translatable("Texture: " + stack.getTag().getString("TextureKey")));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}