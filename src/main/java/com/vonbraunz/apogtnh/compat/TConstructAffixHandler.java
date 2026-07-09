package com.vonbraunz.apogtnh.compat;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.vonbraunz.apogtnh.affix.AffixHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import tconstruct.library.event.ToolBuildEvent;
import tconstruct.library.event.ToolCraftEvent;
import tconstruct.library.event.ToolCraftedEvent;

/**
 * Carries affix data from Tinkers' tool parts through to the assembled tool,
 * including part replacements in the tool station.
 */
public class TConstructAffixHandler {

    private static NBTTagCompound stashedAffixNBT;

    // --- new tool creation (buildTool path) ---------------------------------

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

    // --- output slot (covers both new tools and part replacements) ----------

    /**
     * Fires when the player takes the tool from the output slot.
     * If a part with affix data was used in this build/replacement cycle,
     * overwrites the tool's existing affix data with the new part's data.
     * If no affixed part was used, leaves existing affixes alone.
     */
    @SubscribeEvent
    public void onToolCrafted(ToolCraftedEvent event) {
        if (stashedAffixNBT == null) return;
        ItemStack tool = event.tool;
        if (tool == null) return;

        NBTTagCompound tag = tool.hasTagCompound() ? tool.getTagCompound() : new NBTTagCompound();
        tag.setTag(AffixHelper.ROOT, stashedAffixNBT.copy());
        tool.setTagCompound(tag);
    }
}
