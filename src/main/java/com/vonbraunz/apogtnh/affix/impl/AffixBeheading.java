package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.item.ItemStack;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/**
 * Increased chance for mobs to drop their head/skull. Level = bonus percent chance.
 * The actual skull-drop logic lives in DeadlyEventHandler.onDrops, which checks for
 * this affix on the killer's weapon and rolls a random chance per level.
 */
public class AffixBeheading extends Affix {

    public AffixBeheading() {
        super("apogtnh:beheading", 1, 3, LootCategory.SWORD);
    }

    /** Bonus percent chance per level for a skull to drop. Called from DeadlyEventHandler. */
    public int chancePercent(int level) {
        return level * 5; // 5% / 10% / 15%
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Beheading " + toRoman(level);
    }

    @Override
    public String displayName(int level) {
        return "Cleaving";
    }
}
