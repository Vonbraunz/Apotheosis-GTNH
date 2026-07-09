package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Extra knockback on melee hits. Level = knockback strength. */
public class AffixKnockback extends Affix {

    public AffixKnockback() {
        super("apogtnh:knockback", 1, 3, LootCategory.SWORD);
    }

    @Override
    public void onDamageDealt(ItemStack stack, int level, EntityLivingBase attacker, EntityLivingBase target,
        DamageSource src, float[] amountRef) {
        if (target != null) {
            double dx = target.posX - attacker.posX;
            double dz = target.posZ - attacker.posZ;
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist < 0.01D) dist = 1.0D;
            target.addVelocity((dx / dist) * level * 0.6D, 0.2D * level, (dz / dist) * level * 0.6D);
        }
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "+" + toRoman(level) + " Knockback";
    }

    @Override
    public String displayName(int level) {
        return "Forceful";
    }
}
