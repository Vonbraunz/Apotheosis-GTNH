package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.item.ItemStack;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Bonus damage per arrow hit. Behavior lives in DeadlyEventHandler. */
public class AffixPiercing extends Affix {

    public AffixPiercing() {
        super("apogtnh:piercing", 1, 3, LootCategory.RANGED);
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Piercing " + toRoman(level);
    }

    @Override
    public String displayName(int level) {
        return "Piercing";
    }
}
