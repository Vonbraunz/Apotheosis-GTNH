package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Regeneration potion effect on chestplate wearers. Level = amplifier. */
public class AffixRevitalizing extends Affix {

    public AffixRevitalizing() {
        super("apogtnh:revitalizing", 1, 3, LootCategory.CHESTPLATE);
    }

    @Override
    public void onTick(ItemStack stack, int level, EntityLivingBase holder) {
        if (holder.isPotionActive(Potion.regeneration)) {
            PotionEffect existing = holder.getActivePotionEffect(Potion.regeneration);
            if (existing.getAmplifier() >= level - 1 && existing.getDuration() > 10) return;
        }
        holder.addPotionEffect(new PotionEffect(Potion.regeneration.id, 30, level - 1, true));
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Revitalizing " + toRoman(level);
    }

    @Override
    public String description(int level) {
        return "Grants Regeneration " + toRoman(level) + " while worn.";
    }

    @Override
    public String displayName(int level) {
        return "Revitalizing";
    }
}
