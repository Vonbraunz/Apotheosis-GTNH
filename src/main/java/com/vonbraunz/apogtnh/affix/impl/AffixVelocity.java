package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.item.ItemStack;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Increased arrow velocity. Level = bonus speed multiplier. */
public class AffixVelocity extends Affix {

    public AffixVelocity() {
        super("apogtnh:velocity", 1, 3, LootCategory.RANGED);
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Velocity " + toRoman(level);
    }

    @Override
    public String description(int level) {
        return "Increases arrow speed by " + level + " level(s).";
    }

    @Override
    public String displayName(int level) {
        return "Accelerating";
    }
}
