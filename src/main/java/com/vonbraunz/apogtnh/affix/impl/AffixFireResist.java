package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Fire Resistance potion effect on boot wearers. */
public class AffixFireResist extends Affix {

    public AffixFireResist() {
        super("apogtnh:fire_resist", 1, 1, LootCategory.BOOTS);
    }

    @Override
    public void onTick(ItemStack stack, int level, EntityLivingBase holder) {
        if (holder.isPotionActive(Potion.fireResistance)) {
            PotionEffect existing = holder.getActivePotionEffect(Potion.fireResistance);
            if (existing.getDuration() > 10) return;
        }
        holder.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 30, 0, true));
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Fire Immunity";
    }

    @Override
    public String description(int level) {
        return "Grants permanent Fire Resistance while worn.";
    }

    @Override
    public String displayName(int level) {
        return "Flame-Walker";
    }
}
