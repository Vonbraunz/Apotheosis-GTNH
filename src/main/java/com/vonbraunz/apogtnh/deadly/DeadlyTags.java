package com.vonbraunz.apogtnh.deadly;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Entity tag scheme.
 *
 * All tags live under Entity.getEntityData() (Forge-provided persistent NBT compound).
 * Layout:
 *
 *   entityData:
 *     apogtnh:
 *       tier: STRING (BOSS | ELITE | CARRIER)
 *
 * getEntityData() is server+client shared for the NBT-safe portion and persists across saves.
 */
public class DeadlyTags {

    public static final String ROOT   = "apogtnh";
    public static final String KEY_TIER = "tier";

    public enum Tier {
        BOSS,
        ELITE,
        CARRIER;
    }

    public static void tag(Entity entity, Tier tier) {
        if (entity == null || tier == null) return;
        NBTTagCompound data = entity.getEntityData();
        NBTTagCompound apo = data.getCompoundTag(ROOT);
        apo.setString(KEY_TIER, tier.name());
        data.setTag(ROOT, apo);
    }

    public static Tier getTier(Entity entity) {
        if (entity == null) return null;
        NBTTagCompound data = entity.getEntityData();
        if (!data.hasKey(ROOT, 10)) return null;
        NBTTagCompound apo = data.getCompoundTag(ROOT);
        String name = apo.getString(KEY_TIER);
        if (name == null || name.isEmpty()) return null;
        try {
            return Tier.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static boolean isTagged(Entity entity) {
        return getTier(entity) != null;
    }

    public static boolean isBoss(EntityLivingBase e)    { return getTier(e) == Tier.BOSS; }
    public static boolean isElite(EntityLivingBase e)   { return getTier(e) == Tier.ELITE; }
    public static boolean isCarrier(EntityLivingBase e) { return getTier(e) == Tier.CARRIER; }
}
