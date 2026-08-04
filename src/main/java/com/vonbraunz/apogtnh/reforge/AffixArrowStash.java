package com.vonbraunz.apogtnh.reforge;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Thread-safe stash for passing bow affix NBT between MixinItemBow and MixinEntityArrow.
 * Lives outside the mixin package so it can be freely referenced by both mixins.
 */
public class AffixArrowStash {

    public static NBTTagCompound stashedAffix;
}
