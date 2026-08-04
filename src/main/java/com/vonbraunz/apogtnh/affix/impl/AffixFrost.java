package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Applies slowness to the target on hit. Level = amplifier, duration = 60 ticks. */
public class AffixFrost extends Affix {

    public AffixFrost() {
        super("apogtnh:frost", 1, 3, LootCategory.SWORD);
    }

    @Override
    public void onDamageDealt(ItemStack stack, int level, EntityLivingBase attacker, EntityLivingBase target,
        DamageSource src, float[] amountRef) {
        target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 60, level - 1));
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Frost " + toRoman(level);
    }

    @Override
    public String description(int level) {
        return "Slows the target for 3 seconds. Amplifier increases with level.";
    }

    @Override
    public String displayName(int level) {
        return "Glacial";
    }
}
