package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Instantly kills the target when their HP is below a threshold. Level = threshold percent. */
public class AffixExecute extends Affix {

    public AffixExecute() {
        super("apogtnh:execute", 1, 3, LootCategory.SWORD);
    }

    @Override
    public void onDamageDealt(ItemStack stack, int level, EntityLivingBase attacker, EntityLivingBase target,
        DamageSource src, float[] amountRef) {
        float threshold = level * 0.05F; // 5% / 10% / 15%
        float hpPercent = target.getHealth() / target.getMaxHealth();
        if (hpPercent <= threshold) {
            amountRef[0] = target.getMaxHealth(); // guaranteed kill
        }
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Execute " + toRoman(level);
    }

    @Override
    public String description(int level) {
        return "Instantly kills targets below " + (level * 5) + "% of their max HP.";
    }

    @Override
    public String displayName(int level) {
        return "Executing";
    }
}
