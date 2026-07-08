package com.vonbraunz.apogtnh.affix.impl;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

/** Flat damage reduction when equipped in any armor slot. Level = damage subtracted. */
public class AffixArmor extends Affix {

    public AffixArmor() {
        super("apogtnh:sturdy", 1, 3,
              LootCategory.HELMET, LootCategory.CHESTPLATE,
              LootCategory.LEGGINGS, LootCategory.BOOTS);
    }

    @Override
    public void onDamageTaken(ItemStack stack, int level, EntityLivingBase victim,
                              DamageSource src, float[] amountRef) {
        amountRef[0] = Math.max(0F, amountRef[0] - level);
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "-" + level + " Damage Taken";
    }

    @Override
    public String displayName(int level) {
        return "Sturdy";
    }
}
