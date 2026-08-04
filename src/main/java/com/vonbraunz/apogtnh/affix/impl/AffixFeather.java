package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Fall damage reduction on boots. Level × 15% reduction. */
public class AffixFeather extends Affix {

    public AffixFeather() {
        super("apogtnh:feather", 1, 5, LootCategory.BOOTS);
    }

    @Override
    public void onDamageTaken(ItemStack stack, int level, EntityLivingBase victim, DamageSource src,
        float[] amountRef) {
        if (src == DamageSource.fall) {
            amountRef[0] = Math.max(0F, amountRef[0] * (1.0F - level * 0.15F));
        }
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "-" + (level * 15) + "% Fall Damage";
    }

    @Override
    public String description(int level) {
        return "Reduces fall damage by " + (level * 10) + "%.";
    }

    @Override
    public String displayName(int level) {
        return "Feather";
    }
}
