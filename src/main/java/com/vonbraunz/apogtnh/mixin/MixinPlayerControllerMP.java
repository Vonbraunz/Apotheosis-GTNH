package com.vonbraunz.apogtnh.mixin;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.AffixHelper;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

    @Shadow
    private Minecraft mc;

    /**
     * Extends block reach distance when the player holds a tool with the Reach
     * affix. Vanilla survival distance is ~4.5 blocks; each level adds 1 block.
     */
    @Inject(method = "getBlockReachDistance", at = @At("RETURN"), cancellable = true, remap = false)
    private void apogtnh$extendReach(CallbackInfoReturnable<Float> cir) {
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;
        ItemStack held = player.getHeldItem();
        if (held == null || !AffixHelper.hasAffixData(held)) return;

        for (Map.Entry<Affix, Integer> e : AffixHelper.getAffixes(held)
            .entrySet()) {
            if (e.getKey().id.equals("apogtnh:reach")) {
                cir.setReturnValue(cir.getReturnValue() + e.getValue());
                break;
            }
        }
    }
}
