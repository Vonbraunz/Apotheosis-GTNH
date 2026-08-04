package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.item.ItemStack;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Faster bow charge time. Behavior lives in MixinItemBow. */
public class AffixDrawSpeed extends Affix {

    public AffixDrawSpeed() {
        super("apogtnh:draw_speed", 1, 3, LootCategory.RANGED);
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Draw Speed " + toRoman(level);
    }

    @Override
    public String description(int level) {
        return "Draws the bow " + (int) (level * 33) + "% faster.";
    }

    @Override
    public String displayName(int level) {
        return "Quick-Draw";
    }
}
