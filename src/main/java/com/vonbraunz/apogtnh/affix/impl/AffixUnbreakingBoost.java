package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.item.ItemStack;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/**
 * Percent chance per durability-consuming action to not consume durability, mirroring
 * vanilla Unbreaking math. The actual refund happens in ToolAffixEventHandler via a
 * snapshot-before/compensate-after pattern since 1.7.10 has no clean pre-damage hook.
 */
public class AffixUnbreakingBoost extends Affix {

    public AffixUnbreakingBoost() {
        super("apogtnh:unbreaking_boost", 1, 3, LootCategory.TOOL);
    }

    /** Chance (0-100) that a durability-consuming action gets refunded. Vanilla-style curve. */
    public int chancePercent(int level) {
        return 100 / (level + 1);
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Unbreaking Boost " + toRoman(level);
    }

    @Override
    public String displayName(int level) {
        return "Enduring";
    }
}
