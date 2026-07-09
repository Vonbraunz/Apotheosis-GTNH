package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Resistance potion effect on armor wearers. Level determines amplifier. */
public class AffixFortify extends Affix {

    public AffixFortify() {
        super("apogtnh:fortify", 1, 3, LootCategory.CHESTPLATE);
    }

    @Override
    public void onTick(ItemStack stack, int level, EntityLivingBase holder) {
        if (holder.isPotionActive(Potion.resistance)) {
            PotionEffect existing = holder.getActivePotionEffect(Potion.resistance);
            if (existing.getAmplifier() >= level - 1 && existing.getDuration() > 10) return;
        }
        holder.addPotionEffect(new PotionEffect(Potion.resistance.id, 30, level - 1, true));
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Resistance " + toRoman(level);
    }

    @Override
    public String displayName(int level) {
        return "Fortified";
    }
}
