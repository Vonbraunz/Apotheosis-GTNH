package com.vonbraunz.apogtnh.compat;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.vonbraunz.apogtnh.affix.AffixHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import tconstruct.library.event.ToolBuildEvent;
import tconstruct.library.event.ToolCraftEvent;
import tconstruct.library.event.ToolCraftedEvent;
import tconstruct.library.util.IToolPart;

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

    // --- part replacement (tool station output slot) ------------------------

    /**
     * Fires when the player takes the tool from the output slot, for both new
     * crafts and part replacements. Scans the station's inventory for any part
     * with affix data and injects it into the output tool, replacing stale data.
     */
    @SubscribeEvent
    public void onToolCrafted(ToolCraftedEvent event) {
        ItemStack tool = event.tool;
        if (tool == null) return;

        IInventory inv = event.inventory;
        if (inv == null) return;

        // scan all slots for a Tinkers' part with affix data
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack slot = inv.getStackInSlot(i);
            if (slot == null || !(slot.getItem() instanceof IToolPart)) continue;
            if (!AffixHelper.hasAffixData(slot)) continue;

            NBTTagCompound affixRoot = (NBTTagCompound) slot.getTagCompound()
                .getCompoundTag(AffixHelper.ROOT)
                .copy();
            NBTTagCompound tag = tool.hasTagCompound() ? tool.getTagCompound() : new NBTTagCompound();
            tag.setTag(AffixHelper.ROOT, affixRoot);
            tool.setTagCompound(tag);
            break;
        }
    }
}
