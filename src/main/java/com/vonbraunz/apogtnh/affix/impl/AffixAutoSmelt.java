package com.vonbraunz.apogtnh.affix.impl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Mined blocks drop their smelted result instead of the raw form. */
public class AffixAutoSmelt extends Affix {

    public AffixAutoSmelt() {
        super("apogtnh:auto_smelt", 1, 1, LootCategory.TOOL);
    }

    @Override
    public void onHarvestDrops(ItemStack tool, int level, EntityPlayer player, World world, int x, int y, int z,
        Block block, int meta, List<ItemStack> drops, int vanillaFortune) {
        List<ItemStack> smelted = new ArrayList<ItemStack>();
        for (ItemStack drop : drops) {
            ItemStack result = FurnaceRecipes.smelting()
                .getSmeltingResult(drop);
            if (result != null) {
                ItemStack copy = result.copy();
                copy.stackSize = drop.stackSize;
                smelted.add(copy);
            } else {
                smelted.add(drop);
            }
        }
        drops.clear();
        drops.addAll(smelted);
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Auto-Smelt";
    }

    @Override
    public String description(int level) {
        return "Mined blocks drop their smelted result instead of raw ore.";
    }

    @Override
    public String displayName(int level) {
        return "Molten-Touch";
    }
}
