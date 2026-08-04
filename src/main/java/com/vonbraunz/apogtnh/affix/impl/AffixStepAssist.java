package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Increases the player's step height while wearing boots with this affix. */
public class AffixStepAssist extends Affix {

    public AffixStepAssist() {
        super("apogtnh:step_assist", 1, 3, LootCategory.BOOTS);
    }

    @Override
    public void onTick(ItemStack stack, int level, EntityLivingBase holder) {
        if (!(holder instanceof EntityPlayer)) return;
        // vanilla default is 0.5F; level 1 = 1 block, level 3 = 3 blocks
        holder.stepHeight = level;
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Step Assist " + toRoman(level);
    }

    @Override
    public String description(int level) {
        return "Automatically steps up blocks up to " + level + " block(s) high.";
    }

    @Override
    public String displayName(int level) {
        return "Spring-Step";
    }
}
