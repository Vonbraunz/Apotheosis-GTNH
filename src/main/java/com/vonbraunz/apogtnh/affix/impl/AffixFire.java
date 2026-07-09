package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Lights the target on fire. Level = seconds of burn. */
public class AffixFire extends Affix {

    public AffixFire() {
        super("apogtnh:fire", 1, 5, LootCategory.SWORD);
    }

    @Override
    public void onDamageDealt(ItemStack stack, int level, EntityLivingBase attacker, EntityLivingBase target,
        DamageSource src, float[] amountRef) {
        if (target != null) {
            target.setFire(level * 2);
        }
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "+" + (level * 2) + "s Fire";
    }

    @Override
    public String displayName(int level) {
        return "Scorching";
    }
}
