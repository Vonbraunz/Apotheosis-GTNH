package com.vonbraunz.apogtnh.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Stores the bow's affix NBT (from MixinItemBow's stashed field) on the arrow entity
 * so arrow affixes can be read on impact without needing the player to still be holding
 * the bow.
 */
@Mixin(EntityArrow.class)
public class MixinEntityArrow {

    @Unique
    private static final String APO_ROOT = "apogtnh";

    /**
     * After the arrow is constructed with a shooter, read the stashed bow affix NBT
     * and store it on the arrow's persistent entity data.
     */
    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityLivingBase;F)V", at = @At("RETURN"))
    private void apogtnh$stashAffixOnArrow(World world, EntityLivingBase shooter, float velocity, CallbackInfo ci) {
        NBTTagCompound stashed = MixinItemBow.apogtnh$consumeStashedAffix();
        if (stashed == null) return;
        ((net.minecraft.entity.Entity) (Object) this).getEntityData()
            .setTag(APO_ROOT, stashed);
    }

    /**
     * Exposed for DeadlyEventHandler to check if an arrow carries affix data from a bow.
     */
    @Unique
    public static boolean hasArrowAffixData(EntityArrow arrow) {
        return arrow.getEntityData()
            .hasKey(APO_ROOT, 10);
    }

    /**
     * Returns the bow's full affix NBT compound stored on the arrow, or null.
     */
    @Unique
    public static NBTTagCompound getArrowAffixData(EntityArrow arrow) {
        if (!hasArrowAffixData(arrow)) return null;
        return arrow.getEntityData()
            .getCompoundTag(APO_ROOT);
    }
}
