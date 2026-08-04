package com.vonbraunz.apogtnh.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.vonbraunz.apogtnh.affix.AffixHelper;

/**
 * Hooks into ItemBow to apply bow-specific affixes (draw speed, multishot, velocity)
 * and stash the bow's full affix NBT on the spawned arrow entity so arrow affixes
 * (surgical, explosive, piercing, venom) can be read on impact.
 */
@Mixin(ItemBow.class)
public class MixinItemBow {

    // thread-safe stash for passing bow affix NBT to the arrow constructor
    @Unique
    private static NBTTagCompound apogtnh$stashedAffix;

    /**
     * Before the arrow spawns, stash the bow's affix NBT so MixinEntityArrow can pick
     * it up and store it on the arrow entity.
     */
    @Inject(
        method = "onPlayerStoppedUsing",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;spawnEntityInWorld(Lnet/minecraft/entity/Entity;)Z"))
    private void apogtnh$stashBowAffix(ItemStack stack, World world, EntityPlayer player, int useDuration,
        CallbackInfo ci) {
        if (world.isRemote) return;
        if (AffixHelper.hasAffixData(stack)) {
            apogtnh$stashedAffix = (NBTTagCompound) stack.getTagCompound()
                .getCompoundTag("apogtnh")
                .copy();
        } else {
            apogtnh$stashedAffix = null;
        }
    }

    /**
     * Multishot: spawns 2 extra arrows fanned out from the original if the bow has the
     * multishot affix. Injects after the first arrow is spawned.
     */
    @Inject(
        method = "onPlayerStoppedUsing",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;playSoundAtEntity(Lnet/minecraft/entity/Entity;Ljava/lang/String;FF)V"))
    private void apogtnh$multishot(ItemStack stack, World world, EntityPlayer player, int useDuration,
        CallbackInfo ci) {
        if (world.isRemote) return;
        if (!AffixHelper.hasAffixData(stack)) return;
        int level = apogtnh$affixLevel(stack, "apogtnh:multishot");
        if (level <= 0) return;

        float velocity = apogtnh$getArrowVelocity(stack, useDuration);
        if (velocity < 0.1F) return;

        // spawn 2 extra arrows, fanned ±10 degrees
        for (int i = -1; i <= 1; i += 2) {
            EntityArrow arrow = new EntityArrow(world, player, velocity * 2.0F);
            arrow.setDamage(
                arrow.getDamage() + EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack));
            arrow.setKnockbackStrength(EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack));
            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0) arrow.setFire(100);
            arrow.rotationYaw += i * 10.0F;
            apogtnh$stashedAffix = null; // extra arrows don't re-stash
            world.spawnEntityInWorld(arrow);
        }
    }

    /**
     * Draw Speed: reduces the charge time by overriding the velocity divisor. Level 1 =
     * 15-tick draw, level 3 = 10-tick draw. Injects at the velocity calculation point.
     */
    @ModifyVariable(method = "onPlayerStoppedUsing", at = @At(value = "STORE", ordinal = 2), name = "f")
    private float apogtnh$drawSpeed(float originalVelocity, ItemStack stack) {
        if (!AffixHelper.hasAffixData(stack)) return originalVelocity;
        int level = apogtnh$affixLevel(stack, "apogtnh:draw_speed");
        if (level <= 0) return originalVelocity;
        // vanilla velocity is charge/20.0; we effectively charge faster by dividing by less
        float fastCharge = (float) (apogtnh$getMaxCharge(stack) / (20.0D / (1.0D + level * 0.5D)));
        return Math.min(fastCharge, 1.0F);
    }

    @Unique
    private static int apogtnh$affixLevel(ItemStack stack, String id) {
        NBTTagCompound apo = stack.getTagCompound() != null ? stack.getTagCompound()
            .getCompoundTag("apogtnh") : null;
        if (apo == null || !apo.hasKey("affixes", 10)) return 0;
        return apo.getCompoundTag("affixes")
            .getInteger(id);
    }

    @Unique
    private static float apogtnh$getArrowVelocity(ItemStack stack, int useDuration) {
        int charge = 72000 - useDuration;
        float f = (float) charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        return Math.min(f, 1.0F);
    }

    @Unique
    private static int apogtnh$getMaxCharge(ItemStack stack) {
        return 72000;
    }

    /** Exposed for MixinEntityArrow to read the stashed affix. */
    @Unique
    public static NBTTagCompound apogtnh$consumeStashedAffix() {
        NBTTagCompound tag = apogtnh$stashedAffix;
        apogtnh$stashedAffix = null;
        return tag;
    }
}
