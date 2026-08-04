package com.vonbraunz.apogtnh.affix.impl;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.LootCategory;

/**
 * Right-click with an affixed hoe instantly grows nearby crops one stage, like bonemeal.
 * Calls the same IGrowable methods that vanilla ItemDye.applyBonemeal uses -- func_149851_a
 * (can fertilize check), func_149852_a (should grow), func_149853_b (perform growth).
 * GTNH mappings v12 may provide readable names for these; if not, the func_ names resolve
 * correctly against the IGrowable interface.
 */
public class AffixCropGrowth extends Affix {

    public AffixCropGrowth() {
        super("apogtnh:crop_growth", 1, 2, LootCategory.TOOL);
    }

    @Override
    public boolean onHoeUse(ItemStack tool, int level, EntityPlayer player, World world, int x, int y, int z) {
        int radius = level; // level 1 = 3x3, level 2 = 5x5
        boolean grewAny = false;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int cx = x + dx;
                int cz = z + dz;
                Block block = world.getBlock(cx, y, cz);
                if (block instanceof IGrowable) {
                    IGrowable growable = (IGrowable) block;
                    // func_149851_a = can fertilize check (no Random param in 1.7.10), false = not from bonemeal
                    if (growable.func_149851_a(world, cx, y, cz, false)) {
                        if (!world.isRemote) {
                            // func_149852_a = should perform growth tick
                            if (growable.func_149852_a(world, world.rand, cx, y, cz)) {
                                // func_149853_b = execute growth
                                growable.func_149853_b(world, world.rand, cx, y, cz);
                            }
                        }
                        grewAny = true;
                    }
                }
            }
        }

        return grewAny;
    }

    @Override
    public String tooltip(ItemStack stack, int level) {
        return "Crop Growth " + toRoman(level);
    }

    @Override
    public String description(int level) {
        return "Right-click crops with a hoe to instantly grow them. " + (level == 1 ? "3x3" : "5x5") + " area.";
    }

    @Override
    public String displayName(int level) {
        return "Verdant";
    }
}
