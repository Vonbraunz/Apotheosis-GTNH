package com.vonbraunz.apogtnh.deadly;

import java.util.UUID;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

import com.vonbraunz.apogtnh.placebo.AttributeHelper;

/**
 * Applies stat buffs to elites / bosses and marks them visually.
 *
 * The visual tell for a personal build is intentionally cheap: set a custom name
 * with a colored prefix and enable persistent name tag rendering. Particle-based
 * tells require client-side packet sync — worth adding later, not now.
 */
public class EliteMarker {

    // UUIDs for our attribute modifiers — stable so they don't stack on reload
    private static final UUID UUID_ELITE_HP = UUID.fromString("6b1e40c1-9b1f-4e0b-9ad2-000000000001");
    private static final UUID UUID_ELITE_DMG = UUID.fromString("6b1e40c1-9b1f-4e0b-9ad2-000000000002");
    private static final UUID UUID_BOSS_HP = UUID.fromString("6b1e40c1-9b1f-4e0b-9ad2-000000000003");
    private static final UUID UUID_BOSS_DMG = UUID.fromString("6b1e40c1-9b1f-4e0b-9ad2-000000000004");
    private static final UUID UUID_BOSS_KB = UUID.fromString("6b1e40c1-9b1f-4e0b-9ad2-000000000005");

    // operation constants (vanilla): 0 = additive, 1 = multiply-add, 2 = multiply
    public static void applyElite(EntityLivingBase entity) {
        AttributeHelper.applyBuff(entity, AttributeHelper.maxHealth(), UUID_ELITE_HP, "apogtnh.elite.hp", 1.5D, 2); // +150%
                                                                                                                    // max
                                                                                                                    // HP
        AttributeHelper.applyBuff(entity, AttributeHelper.attackDamage(), UUID_ELITE_DMG, "apogtnh.elite.dmg", 0.5D, 2); // +50%
                                                                                                                         // damage
        AttributeHelper.healToFull(entity);

        // Visual tell: name + always-visible tag (methods on EntityLiving, not EntityLivingBase)
        EntityLiving living = (EntityLiving) entity;
        living.setCustomNameTag("\u00a76Elite \u00a7r" + living.getCommandSenderName());
        living.setAlwaysRenderNameTag(true);
    }

    public static void applyBoss(EntityLivingBase entity) {
        AttributeHelper.applyBuff(entity, AttributeHelper.maxHealth(), UUID_BOSS_HP, "apogtnh.boss.hp", 4.0D, 2); // +400%
                                                                                                                  // max
                                                                                                                  // HP
        AttributeHelper.applyBuff(entity, AttributeHelper.attackDamage(), UUID_BOSS_DMG, "apogtnh.boss.dmg", 1.5D, 2); // +150%
                                                                                                                       // damage
        AttributeHelper.applyBuff(entity, AttributeHelper.knockbackResist(), UUID_BOSS_KB, "apogtnh.boss.kb", 1.0D, 0); // full
                                                                                                                        // knockback
                                                                                                                        // resist
        AttributeHelper.healToFull(entity);

        EntityLiving living = (EntityLiving) entity;
        living.setCustomNameTag("\u00a7c\u00a7lBoss \u00a7r" + living.getCommandSenderName());
        living.setAlwaysRenderNameTag(true);
    }
}
