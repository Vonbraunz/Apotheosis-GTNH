package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Jump boost on boots. Level determines amplifier. */
public class AffixGravity extends Affix {

    public AffixGravity() {
        super("apogtnh:gravity", 1, 3, LootCategory.BOOTS);
    }

    @Override
    public void onTick(ItemStack stack, int level, EntityLivingBase holder) {
        if (holder.isPotionActive(Potion.jump)) {
            PotionEffect existing = holder.getActivePotionEffect(Potion.jump);
            if (existing.getAmplifier() >= level - 1 && existing.getDuration() > 10) return;
        }
        holder.addPotionEffect(new PotionEffect(Potion.jump.id, 30, level - 1, true));
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Jump Boost " + toRoman(level);
    }

    @Override
    public String displayName(int level) {
        return "Spring";
    }
}
