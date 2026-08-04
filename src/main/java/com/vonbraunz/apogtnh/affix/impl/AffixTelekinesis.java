package com.vonbraunz.apogtnh.affix.impl;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/** Mined block drops go straight into the player's inventory. Falls back to world-spawn if full. */
public class AffixTelekinesis extends Affix {

    public AffixTelekinesis() {
        super("apogtnh:telekinesis", 1, 1, LootCategory.TOOL);
    }

    @Override
    public void onHarvestDrops(ItemStack tool, int level, EntityPlayer player, World world, int x, int y, int z,
        Block block, int meta, List<ItemStack> drops, int vanillaFortune) {
        List<ItemStack> leftovers = new java.util.ArrayList<ItemStack>();
        for (ItemStack drop : drops) {
            if (!player.inventory.addItemStackToInventory(drop.copy())) {
                leftovers.add(drop);
            }
        }
        drops.clear();
        // whatever didn't fit falls back to normal world-spawn
        for (ItemStack leftover : leftovers) {
            if (!world.isRemote) {
                EntityItem entity = new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D, leftover);
                world.spawnEntityInWorld(entity);
            }
        }
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Telekinesis";
    }

    @Override
    public String description(int level) {
        return "Mined block drops go directly into your inventory.";
    }

    @Override
    public String displayName(int level) {
        return "Reaching";
    }
}
