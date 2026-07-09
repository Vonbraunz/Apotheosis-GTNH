package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Extra flat damage reduction, stacks across armor pieces. Level = hearts reduced. */
public class AffixGuardian extends Affix {

    public AffixGuardian() {
        super(
            "apogtnh:guardian",
            1,
            3,
            LootCategory.HELMET,
            LootCategory.CHESTPLATE,
            LootCategory.LEGGINGS,
            LootCategory.BOOTS);
    }

    @Override
    public void onDamageTaken(ItemStack stack, int level, EntityLivingBase victim, DamageSource src,
        float[] amountRef) {
        amountRef[0] = Math.max(0F, amountRef[0] - level * 0.5F);
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "-" + toRoman(level) + " Damage";
    }

    @Override
    public String displayName(int level) {
        return "Guardian";
    }
}
