package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/**
 * Self-repair on weapons and tools. Level controls repair speed. Handles both vanilla damage and Tinkers' InFiTool NBT
 * durability.
 */
public class AffixMending extends Affix {

    public AffixMending() {
        super("apogtnh:mending", 1, 5, LootCategory.SWORD, LootCategory.TOOL);
    }

    @Override
    public void onTick(ItemStack stack, int level, EntityLivingBase holder) {
        int current = getDamage(stack);
        if (current <= 0) return;
        int interval = Math.max(1, 6 - level);
        if (holder.ticksExisted % interval == 0) {
            setDamage(stack, current - 1);
        }
    }

    private int getDamage(ItemStack stack) {
        // Tinkers' Construct stores damage in InFiTool NBT, not vanilla item damage
        if (stack.hasTagCompound() && stack.getTagCompound()
            .hasKey("InfiTool", 10)) {
            NBTTagCompound infi = stack.getTagCompound()
                .getCompoundTag("InfiTool");
            return infi.getInteger("Damage");
        }
        return stack.getItemDamage();
    }

    private void setDamage(ItemStack stack, int damage) {
        if (stack.hasTagCompound() && stack.getTagCompound()
            .hasKey("InfiTool", 10)) {
            NBTTagCompound infi = stack.getTagCompound()
                .getCompoundTag("InfiTool");
            infi.setInteger("Damage", Math.max(0, damage));
            return;
        }
        stack.setItemDamage(Math.max(0, damage));
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
