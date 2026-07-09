package com.vonbraunz.apogtnh.affix.impl;

import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Movement speed boost on boots. Level × 5% speed. */
public class AffixMovement extends Affix {

    private static final UUID MODIFIER_ID = UUID.fromString("e1b40c10-9b1f-4e0b-9ad2-000000000010");

    public AffixMovement() {
        super("apogtnh:swift", 1, 4, LootCategory.BOOTS);
    }

    @Override
    public void onTick(ItemStack stack, int level, EntityLivingBase holder) {
        IAttributeInstance attr = holder.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        if (attr == null) return;
        AttributeModifier existing = attr.getModifier(MODIFIER_ID);
        if (existing != null && existing.getAmount() == level * 0.05D) return;
        if (existing != null) attr.removeModifier(existing);
        attr.applyModifier(new AttributeModifier(MODIFIER_ID, "apogtnh.swift", level * 0.05D, 2));
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "+" + toRoman(level) + "% Speed";
    }

    @Override
    public String displayName(int level) {
        return "Swift";
    }
}
