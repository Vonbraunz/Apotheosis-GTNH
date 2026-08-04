package com.vonbraunz.apogtnh.affix.impl;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Hits multiple nearby enemies on each swing. Level 1 = 3 targets, level 3 = 5 targets. */
public class AffixCleaving extends Affix {

    public AffixCleaving() {
        super("apogtnh:cleaving", 1, 3, LootCategory.SWORD, LootCategory.TOOL);
    }

    @Override
    public void onDamageDealt(ItemStack stack, int level, EntityLivingBase attacker, EntityLivingBase target,
        DamageSource src, float[] amountRef) {
        int maxExtra = level + 2; // 3 / 4 / 5
        double range = level + 1.0D; // 2 / 3 / 4 blocks
        AxisAlignedBB aabb = target.boundingBox.expand(range, 1.0D, range);
        List<EntityLivingBase> nearby = attacker.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, aabb);

        int hit = 0;
        for (EntityLivingBase e : nearby) {
            if (e == target || e == attacker || e.isDead) continue;
            e.attackEntityFrom(src, amountRef[0]);
            hit++;
            if (hit >= maxExtra) break;
        }
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Cleaving " + toRoman(level);
    }

    @Override
    public String description(int level) {
        return "Hits up to " + (level + 2) + " nearby enemies within " + (level + 1) + " blocks.";
    }

    @Override
    public String displayName(int level) {
        return "Cleaving";
    }
}
