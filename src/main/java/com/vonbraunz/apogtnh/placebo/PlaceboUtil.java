package com.vonbraunz.apogtnh.placebo;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Minimal port of Placebo NBT + collection utilities.
 * Only the surface actually consumed by the affix and reforge code.
 */
public class PlaceboUtil {

    /** Get (or lazily create) the stack's root NBT compound. */
    public static NBTTagCompound getStackNBT(ItemStack stack) {
        if (stack == null) return null;
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        return stack.getTagCompound();
    }

    /** Get (or create) a nested compound under a top-level key. */
    public static NBTTagCompound getOrCreateSub(NBTTagCompound parent, String key) {
        if (!parent.hasKey(key, 10 /* NBTTagCompound */)) {
            parent.setTag(key, new NBTTagCompound());
        }
        return parent.getCompoundTag(key);
    }
}
