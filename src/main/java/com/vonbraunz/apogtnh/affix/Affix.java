package com.vonbraunz.apogtnh.affix;

import java.util.EnumSet;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

/**
 * Base class for all affixes.
 *
 * Contract:
 * - id is a unique registry key ({@code "apogtnh:sharp"}, etc.)
 * - {@link #canApplyTo(LootCategory)} filters the pool at roll-time
 * - Firing points (damage/take/tick/onEquip) are called from {@code DeadlyEventHandler}
 *
 * Rolling values ({@code level} in NBT) are affix-defined and stored per-item.
 */
public abstract class Affix {

    public final String id;
    public final EnumSet<LootCategory> categories;
    public final int minLevel;
    public final int maxLevel;

    public Affix(String id, int minLevel, int maxLevel, LootCategory... categories) {
        this.id = id;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.categories = EnumSet.noneOf(LootCategory.class);
        for (LootCategory c : categories) this.categories.add(c);
    }

    public boolean canApplyTo(LootCategory cat) {
        return cat != null && (categories.contains(LootCategory.ANY) || categories.contains(cat));
    }

    // ---- Firing points (default no-ops; override what you need) ---------------

    /** Called from LivingHurtEvent when the wearer/wielder is dealing damage. */
    public void onDamageDealt(ItemStack stack, int level, EntityLivingBase attacker, EntityLivingBase target,
        DamageSource src, float[] amountRef) {
        // amountRef[0] is the mutable damage — write back to modify
    }

    /** Called from LivingHurtEvent when the wearer is taking damage (armor slots). */
    public void onDamageTaken(ItemStack stack, int level, EntityLivingBase victim, DamageSource src,
        float[] amountRef) {}

    /** Called each tick from LivingUpdateEvent for equipped/held items. */
    public void onTick(ItemStack stack, int level, EntityLivingBase holder) {}

    /** Tooltip line to inject under the affix header. Return null to skip. */
    public String tooltip(ItemStack stack, int level) {
        return null;
    }

    /** Roll a level within [minLevel, maxLevel]. Override for non-uniform curves. */
    public int rollLevel(java.util.Random rand) {
        if (minLevel >= maxLevel) return minLevel;
        return minLevel + rand.nextInt(maxLevel - minLevel + 1);
    }

    /** Human-readable name used in item display name construction. */
    public abstract String displayName(int level);
}
