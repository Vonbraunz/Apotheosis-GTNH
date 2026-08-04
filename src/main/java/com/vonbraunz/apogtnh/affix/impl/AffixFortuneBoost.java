package com.vonbraunz.apogtnh.affix.impl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Adds bonus fortune levels to the tool's drop calculation at harvest time. */
public class AffixFortuneBoost extends Affix {

    public AffixFortuneBoost() {
        super("apogtnh:fortune_boost", 1, 3, LootCategory.TOOL);
    }

    @Override
    public void onHarvestDrops(ItemStack tool, int level, EntityPlayer player, World world, int x, int y, int z,
        Block block, int meta, List<ItemStack> drops, int vanillaFortune) {
        // recompute drops with the boosted fortune level so smelting (which runs after
        // this) sees the higher quantity
        ArrayList<ItemStack> boosted = block.getDrops(world, x, y, z, meta, vanillaFortune + level);
        drops.clear();
        drops.addAll(boosted);
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Fortune Boost " + toRoman(level);
    }

    @Override
    public String displayName(int level) {
        return "Lucky-Strike";
    }
}
