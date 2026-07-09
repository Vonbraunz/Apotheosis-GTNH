package com.vonbraunz.apogtnh.affix.impl;

import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Max health boost on chestplate/leggings. Level × 2 hearts. */
public class AffixHeart extends Affix {

    private static final UUID MODIFIER_ID = UUID.fromString("e1b40c10-9b1f-4e0b-9ad2-000000000011");

    public AffixHeart() {
        super("apogtnh:heart", 1, 5, LootCategory.CHESTPLATE, LootCategory.LEGGINGS);
    }

    @Override
    public void onTick(ItemStack stack, int level, EntityLivingBase holder) {
        IAttributeInstance attr = holder.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        if (attr == null) return;
        AttributeModifier existing = attr.getModifier(MODIFIER_ID);
        double target = level * 2.0D; // each level = +1 heart (2 HP)
        if (existing != null && existing.getAmount() == target) return;
        if (existing != null) attr.removeModifier(existing);
        attr.applyModifier(new AttributeModifier(MODIFIER_ID, "apogtnh.heart", target, 0));
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "+" + (level * 2) + " Max Health";
    }

    @Override
    public String displayName(int level) {
        return "Vital";
    }
}
