package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.item.ItemStack;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Applies poison on arrow hit. Behavior lives in DeadlyEventHandler. */
public class AffixVenom extends Affix {

    public AffixVenom() {
        super("apogtnh:venom", 1, 3, LootCategory.RANGED);
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Venom " + toRoman(level);
    }

    @Override
    public String displayName(int level) {
        return "Venomous";
    }
}
