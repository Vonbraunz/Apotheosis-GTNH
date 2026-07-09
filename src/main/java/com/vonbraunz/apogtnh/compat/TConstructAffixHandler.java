package com.vonbraunz.apogtnh.compat;

import net.minecraft.nbt.NBTTagCompound;

import com.vonbraunz.apogtnh.affix.AffixHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import tconstruct.library.event.ToolBuildEvent;
import tconstruct.library.event.ToolCraftEvent;

/**
 * Carries affix data from Tinkers' tool parts through to the assembled tool.
 *
 * When a player crafts a tool using a part that has affix data, the ToolBuilder
 * creates fresh NBT and discards the original part's data. We intercept the build
 * event to snapshot the head stack's affix NBT, then inject it into every subsequent
 * craft event until the head changes. The tool station calls buildTool multiple times
 * (preview + actual craft), so we don't clear the stash on first use.
 */
public class TConstructAffixHandler {

    private static NBTTagCompound stashedAffixNBT;

    @SubscribeEvent
    public void onToolBuild(ToolBuildEvent event) {
        if (event.headStack != null && AffixHelper.hasAffixData(event.headStack)) {
            stashedAffixNBT = (NBTTagCompound) event.headStack.getTagCompound()
                .getCompoundTag(AffixHelper.ROOT)
                .copy();
        } else {
            stashedAffixNBT = null;
        }
    }

    @SubscribeEvent
    public void onToolCraft(ToolCraftEvent.NormalTool event) {
        if (stashedAffixNBT == null) return;
        event.toolTag.setTag(AffixHelper.ROOT, stashedAffixNBT.copy());
    }
}
