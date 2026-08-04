package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Water Breathing potion effect on helmet wearers. */
public class AffixAquatic extends Affix {

    public AffixAquatic() {
        super("apogtnh:aquatic", 1, 1, LootCategory.HELMET);
    }

    @Override
    public void onTick(ItemStack stack, int level, EntityLivingBase holder) {
        if (holder.isPotionActive(Potion.waterBreathing)) {
            PotionEffect existing = holder.getActivePotionEffect(Potion.waterBreathing);
            if (existing.getDuration() > 10) return;
        }
        holder.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 30, 0, true));
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Water Breathing";
    }

    @Override
    public String displayName(int level) {
        return "Aquatic";
    }
}
