package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.item.ItemStack;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Bonus critical damage on arrow hit. Behavior lives in DeadlyEventHandler. */
public class AffixSurgical extends Affix {

    public AffixSurgical() {
        super("apogtnh:surgical", 1, 3, LootCategory.RANGED);
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Surgical " + toRoman(level);
    }

    @Override
    public String description(int level) {
        return "Deals " + (level * 25) + "% bonus critical damage on arrow hit.";
    }

    @Override
    public String displayName(int level) {
        return "Surgical";
    }
}
