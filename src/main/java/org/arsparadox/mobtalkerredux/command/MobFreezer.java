package org.arsparadox.mobtalkerredux.command;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MobFreezer {
    private static final Map<UUID, Boolean> frozenMobs = new HashMap<>();
    private static final UUID MOVEMENT_SPEED_MODIFIER = UUID.fromString("708396DC-7DEA-4EDD-B915-A3B97ADFF457");

    /**
     * Freezes a mob in place, disabling AI and movement
     * @param mob The mob to freeze
     * @return true if the mob was frozen, false if already frozen
     */
    public static boolean freezeMob(Mob mob) {
        if (mob == null || frozenMobs.containsKey(mob.getUUID())) {
            return false;
        }

        // Store current AI state
        frozenMobs.put(mob.getUUID(), mob.isNoAi());

        // Disable AI
        mob.setNoAi(true);

        // Stop all current goals
//        mob.goalSelector.removeAllGoals();
//        mob.targetSelector.removeAllGoals();

        // Freeze movement speed
        mob.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(
                new AttributeModifier(MOVEMENT_SPEED_MODIFIER, "Freeze movement", -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL)
        );

        // Stop any current movement/path
        mob.getNavigation().stop();
        mob.setDeltaMovement(0, mob.getDeltaMovement().y, 0); // Preserve Y movement for gravity

        return true;
    }

    /**
     * Freezes a mob by UUID if it exists in the world
     * @param level The server level to search in
     * @param uuid The UUID of the mob to freeze
     * @return true if the mob was found and frozen
     */
    public static boolean freezeMobByUUID(ServerLevel level, UUID uuid) {
        if (level.getEntity(uuid) instanceof Mob mob) {
            return freezeMob(mob);
        }
        return false;
    }

    /**
     * Unfreezes a previously frozen mob
     * @param mob The mob to unfreeze
     * @return true if the mob was unfrozen, false if it wasn't frozen
     */
    public static boolean unfreezeMob(Mob mob) {
        if (mob == null || !frozenMobs.containsKey(mob.getUUID())) {
            return false;
        }

        // Restore original AI state
        mob.setNoAi(frozenMobs.remove(mob.getUUID()));

        // Remove movement speed modifier
        mob.getAttribute(Attributes.MOVEMENT_SPEED)
                .removeModifier(MOVEMENT_SPEED_MODIFIER);

        // The mob's AI goals will be reinstated automatically when needed
        return true;
    }

    /**
     * Unfreezes a mob by UUID if it exists in the world
     * @param level The server level to search in
     * @param uuid The UUID of the mob to unfreeze
     * @return true if the mob was found and unfrozen
     */
    public static boolean unfreezeMobByUUID(ServerLevel level, UUID uuid) {
        if (level.getEntity(uuid) instanceof Mob mob) {
            return unfreezeMob(mob);
        }
        return false;
    }

    /**
     * Checks if a mob is currently frozen
     */
    public static boolean isFrozen(UUID uuid) {
        return frozenMobs.containsKey(uuid);
    }
}