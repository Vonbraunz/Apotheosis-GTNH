package com.vonbraunz.apogtnh.affix.impl;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

/** +damage on melee/ranged hits. Level = flat extra hearts of damage. */
public class AffixDamage extends Affix {

    public AffixDamage() {
        super("apogtnh:sharp", 1, 4, LootCategory.SWORD, LootCategory.RANGED);
    }

    @Override
    public void onDamageDealt(ItemStack stack, int level, EntityLivingBase attacker,
                              EntityLivingBase target, DamageSource src, float[] amountRef) {
        amountRef[0] += level;
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "+" + level + " Damage";
    }

    @Override
    public String displayName(int level) {
        return "Sharp";
    }
}
