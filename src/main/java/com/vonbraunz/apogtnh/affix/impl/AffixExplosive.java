package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.item.ItemStack;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** TNT explosion on arrow impact. Behavior lives in DeadlyEventHandler. */
public class AffixExplosive extends Affix {

    public AffixExplosive() {
        super("apogtnh:explosive", 1, 1, LootCategory.RANGED);
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Explosive";
    }

    @Override
    public String displayName(int level) {
        return "Blasting";
    }
}
