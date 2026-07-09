package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Self-repair on weapons and tools. Level controls repair speed. */
public class AffixMending extends Affix {

    public AffixMending() {
        super("apogtnh:mending", 1, 5, LootCategory.SWORD, LootCategory.TOOL);
    }

    // tick counter per holder -- simple modulo approach
    @Override
    public void onTick(ItemStack stack, int level, EntityLivingBase holder) {
        if (stack.getItemDamage() <= 0) return;
        // repair every (6 - level) ticks; level 5 repairs every tick
        int interval = Math.max(1, 6 - level);
        if (holder.ticksExisted % interval == 0) {
            stack.setItemDamage(stack.getItemDamage() - 1);
        }
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Self-Repair " + (level);
    }

    @Override
    public String displayName(int level) {
        return "Mending";
    }
}
