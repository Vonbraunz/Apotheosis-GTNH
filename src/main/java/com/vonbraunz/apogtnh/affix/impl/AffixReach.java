package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.item.ItemStack;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/**
 * Increases block interaction range. Level = extra blocks of reach.
 * TODO: requires mixin/ASM to modify PlayerControllerMP reach distance in 1.7.10.
 * Currently registered but non-functional until the reach hook is implemented.
 */
public class AffixReach extends Affix {

    public AffixReach() {
        super("apogtnh:reach", 1, 3, LootCategory.TOOL);
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Reach " + toRoman(level);
    }

    @Override
    public String description(int level) {
        return "Extends block interaction range by " + level + " block(s).";
    }

    @Override
    public String displayName(int level) {
        return "Extending";
    }
}
