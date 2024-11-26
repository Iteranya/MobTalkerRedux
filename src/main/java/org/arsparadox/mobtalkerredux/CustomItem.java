package org.arsparadox.mobtalkerredux;

//public class CustomItem extends Item {
//    // No Like, Literally Custom Item
//
//    public CustomItem() {
//        super(new Item.Properties());
//    }
//
//    @Override
//    public Component getName(ItemStack stack) {
//        CompoundTag tag = stack.getTag();
//        if (tag != null && tag.contains("CustomName")) {
//            return Component.translatable(tag.getString("CustomName"));
//        }
//        return super.getName(stack);
//    }
//
//    @Override
//    public void appendHoverText(ItemStack stack, TooltipType tooltipType, List<Component> tooltip, Level level) {
//        CompoundTag tag = stack.getTag();
//        if (tag != null && tag.contains("Description")) {
//            tooltip.add(Component.translatable("Description: " + tag.getString("Description")));
//        }
//        super.appendHoverText(stack, tooltipType, tooltip, level);
//    }
//}