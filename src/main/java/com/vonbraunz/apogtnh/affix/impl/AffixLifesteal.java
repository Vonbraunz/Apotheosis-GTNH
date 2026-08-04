package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Percent of damage dealt is returned as healing. Level = percent (1..10). */
public class AffixLifesteal extends Affix {

    public AffixLifesteal() {
        super("apogtnh:vampiric", 1, 10, LootCategory.SWORD, LootCategory.RANGED);
    }

    @Override
    public void onDamageDealt(ItemStack stack, int level, EntityLivingBase attacker, EntityLivingBase target,
        DamageSource src, float[] amountRef) {
        if (attacker == null || amountRef[0] <= 0) return;
        float heal = amountRef[0] * (level / 100.0F);
        attacker.heal(heal);
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return level + "% Lifesteal";
    }

    @Override
    public String description(int level) {
        return "Heals you for " + level + "% of the damage you deal.";
    }

    @Override
    public String displayName(int level) {
        return "Vampiric";
    }
}
