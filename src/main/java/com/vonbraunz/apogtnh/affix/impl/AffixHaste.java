package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Haste potion effect on tool holders. Level determines amplifier. */
public class AffixHaste extends Affix {

    public AffixHaste() {
        super("apogtnh:haste", 1, 3, LootCategory.TOOL);
    }

    @Override
    public void onTick(ItemStack stack, int level, EntityLivingBase holder) {
        if (holder.isPotionActive(Potion.digSpeed)) {
            PotionEffect existing = holder.getActivePotionEffect(Potion.digSpeed);
            if (existing.getAmplifier() >= level - 1 && existing.getDuration() > 10) return;
        }
        holder.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 30, level - 1, true));
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Haste " + toRoman(level);
    }

    @Override
    public String displayName(int level) {
        return "Swift-Hand";
    }
}
