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

import com.vonbraunz.apogtnh.reforge.AffixArrowStash;

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
        NBTTagCompound stashed = AffixArrowStash.stashedAffix;
        AffixArrowStash.stashedAffix = null;
        if (stashed == null) return;
        ((net.minecraft.entity.Entity) (Object) this).getEntityData()
            .setTag(APO_ROOT, stashed);
    }
}
