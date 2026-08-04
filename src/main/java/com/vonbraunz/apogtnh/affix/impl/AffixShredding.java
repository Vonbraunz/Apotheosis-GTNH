package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Bonus damage as a percentage of the target's max HP. Level * 5 = percent. */
public class AffixShredding extends Affix {

    public AffixShredding() {
        super("apogtnh:shredding", 1, 3, LootCategory.SWORD, LootCategory.TOOL);
    }

    @Override
    public void onDamageDealt(ItemStack stack, int level, EntityLivingBase attacker, EntityLivingBase target,
        DamageSource src, float[] amountRef) {
        amountRef[0] += target.getMaxHealth() * (level * 0.05F); // 5% / 10% / 15%
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Shredding " + toRoman(level);
    }

    @Override
    public String displayName(int level) {
        return "Shredding";
    }
}
