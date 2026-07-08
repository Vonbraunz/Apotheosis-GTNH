package com.vonbraunz.apogtnh.placebo;

import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

/**
 * Attribute modifier helpers for boss/elite buffing.
 * 1.7.10 has a working IAttribute system so this is a straight port.
 */
public class AttributeHelper {

    public static void applyBuff(EntityLivingBase entity, IAttribute attr, UUID id, String name,
                                 double amount, int operation) {
        IAttributeInstance inst = entity.getEntityAttribute(attr);
        if (inst == null) return;
        AttributeModifier existing = inst.getModifier(id);
        if (existing != null) inst.removeModifier(existing);
        inst.applyModifier(new AttributeModifier(id, name, amount, operation));
    }

    /** Full-heal after buffing so max-health increases actually restore the mob. */
    public static void healToFull(EntityLivingBase entity) {
        entity.setHealth(entity.getMaxHealth());
    }

    // Common vanilla attribute handles for convenience
    public static IAttribute maxHealth()   { return SharedMonsterAttributes.maxHealth; }
    public static IAttribute attackDamage(){ return SharedMonsterAttributes.attackDamage; }
    public static IAttribute movementSpeed(){ return SharedMonsterAttributes.movementSpeed; }
    public static IAttribute knockbackResist(){ return SharedMonsterAttributes.knockbackResistance; }
}
