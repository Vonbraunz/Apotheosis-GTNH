package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.item.ItemStack;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Fires 3 arrows instead of 1. Behavior lives in MixinItemBow. */
public class AffixMultishot extends Affix {

    public AffixMultishot() {
        super("apogtnh:multishot", 1, 1, LootCategory.RANGED);
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Multishot";
    }

    @Override
    public String description(int level) {
        return "Fires 3 arrows instead of 1 per shot.";
    }

    @Override
    public String displayName(int level) {
        return "Multishot";
    }
}
