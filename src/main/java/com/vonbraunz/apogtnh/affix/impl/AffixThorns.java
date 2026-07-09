package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Reflects a percentage of incoming melee damage back to the attacker. Level = reflect percent. */
public class AffixThorns extends Affix {

    public AffixThorns() {
        super("apogtnh:thorns", 5, 30, LootCategory.CHESTPLATE);
    }

    @Override
    public void onDamageTaken(ItemStack stack, int level, EntityLivingBase victim, DamageSource src,
        float[] amountRef) {
        if (src instanceof EntityDamageSource) {
            EntityLivingBase attacker = (EntityLivingBase) src.getEntity();
            if (attacker != null && attacker != victim) {
                float reflect = amountRef[0] * (level / 100.0F);
                if (reflect > 0.5F) {
                    attacker.attackEntityFrom(DamageSource.causeThornsDamage(victim), reflect);
                }
            }
        }
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return level + "% Thorns";
    }

    @Override
    public String displayName(int level) {
        return "Barbed";
    }
}
