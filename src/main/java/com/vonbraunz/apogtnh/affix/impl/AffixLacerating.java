package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Percent chance to deal double damage. Level * 10 = percent chance. */
public class AffixLacerating extends Affix {

    public AffixLacerating() {
        super("apogtnh:lacerating", 1, 3, LootCategory.SWORD);
    }

    @Override
    public void onDamageDealt(ItemStack stack, int level, EntityLivingBase attacker, EntityLivingBase target,
        DamageSource src, float[] amountRef) {
        if (attacker.worldObj.rand.nextInt(100) < level * 10) {
            amountRef[0] *= 2.0F;
        }
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Lacerating " + toRoman(level);
    }

    @Override
    public String description(int level) {
        return "Grants " + (level * 10) + "% chance to deal double damage.";
    }

    @Override
    public String displayName(int level) {
        return "Lacerating";
    }
}
