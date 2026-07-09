package com.vonbraunz.apogtnh.compat;

import java.util.Random;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.TinkerTools.MaterialID;

/**
 * Tinkers' Construct item pool for GTNH.
 *
 * Low-rarity drops are tool head parts (player assembles with their own rod/binding).
 * Epic/Mythic drops are fully assembled tools with modifiers pre-applied.
 * Armor stays vanilla diamond -- Tinkers' has no armor in 1.7.10.
 */
public class TinkersItems {

    // --- Material pools by rarity -------------------------------------------

    private static final int[] MATS_COMMON = { MaterialID.Stone, MaterialID.Flint, MaterialID.Iron, MaterialID.Copper };
    private static final int[] MATS_UNCOMMON = { MaterialID.Bronze, MaterialID.Bone, MaterialID.Cactus,
        MaterialID.Slime };
    private static final int[] MATS_RARE = { MaterialID.Steel, MaterialID.Alumite, MaterialID.PigIron };
    private static final int[] MATS_EPIC = { MaterialID.Cobalt, MaterialID.Ardite };
    private static final int[] MATS_MYTHIC = { MaterialID.Manyullyn, MaterialID.Thaumium };

    // --- Part drops (common-rare) -------------------------------------------

    /** Random tool head part with material gated by rarity tier. */
    public static ItemStack createPart(Random rand, int tier) {
        // tier 0=common, 1=uncommon, 2=rare
        int[] pool;
        switch (tier) {
            case 1:
                pool = MATS_UNCOMMON;
                break;
            case 2:
                pool = MATS_RARE;
                break;
            default:
                pool = MATS_COMMON;
                break;
        }
        int mat = pool[rand.nextInt(pool.length)];

        // pick a random head type: 0=sword blade, 1=pick head, 2=axe head, 3=shovel head
        switch (rand.nextInt(4)) {
            case 0:
                return new ItemStack(TinkerTools.swordBlade, 1, mat);
            case 1:
                return new ItemStack(TinkerTools.pickaxeHead, 1, mat);
            case 2:
                return new ItemStack(TinkerTools.hatchetHead, 1, mat);
            default:
                return new ItemStack(TinkerTools.shovelHead, 1, mat);
        }
    }

    // --- Assembled tool drops (epic/mythic) ---------------------------------

    /** Fully assembled Tinkers' tool with modifiers pre-applied. Ranged stays vanilla. */
    public static ItemStack createTool(Random rand, boolean mythic) {
        int[] pool = mythic ? MATS_MYTHIC : MATS_EPIC;
        int mat = pool[rand.nextInt(pool.length)];

        switch (rand.nextInt(4)) {
            case 0:
                return createBroadsword(mat, mythic);
            case 1:
                return createPickaxe(mat, mythic);
            case 2:
                return createHatchet(mat, mythic);
            default:
                return createShovel(mat, mythic);
        }
    }

    private static ItemStack createBroadsword(int mat, boolean mythic) {
        ItemStack blade = new ItemStack(TinkerTools.swordBlade, 1, mat);
        ItemStack rod = new ItemStack(TinkerTools.toolRod, 1, mat);
        ItemStack guard = new ItemStack(TinkerTools.wideGuard, 1, mat);
        ItemStack tool = ToolBuilder.instance.buildTool(blade, rod, guard, null, "");
        if (tool != null) applyQuartz(tool, mythic);
        return tool;
    }

    private static ItemStack createPickaxe(int mat, boolean mythic) {
        ItemStack head = new ItemStack(TinkerTools.pickaxeHead, 1, mat);
        ItemStack rod = new ItemStack(TinkerTools.toolRod, 1, mat);
        ItemStack binding = new ItemStack(TinkerTools.binding, 1, mat);
        ItemStack tool = ToolBuilder.instance.buildTool(head, rod, binding, null, "");
        if (tool != null) applyLapis(tool, mythic);
        if (tool != null) applyRedstone(tool, mythic);
        return tool;
    }

    private static ItemStack createHatchet(int mat, boolean mythic) {
        ItemStack head = new ItemStack(TinkerTools.hatchetHead, 1, mat);
        ItemStack rod = new ItemStack(TinkerTools.toolRod, 1, mat);
        ItemStack tool = ToolBuilder.instance.buildTool(head, rod, null, null, "");
        if (tool != null) applyQuartz(tool, mythic);
        return tool;
    }

    private static ItemStack createShovel(int mat, boolean mythic) {
        ItemStack head = new ItemStack(TinkerTools.shovelHead, 1, mat);
        ItemStack rod = new ItemStack(TinkerTools.toolRod, 1, mat);
        ItemStack tool = ToolBuilder.instance.buildTool(head, rod, null, null, "");
        if (tool != null) applyRedstone(tool, mythic);
        return tool;
    }

    // --- Modifier application -----------------------------------------------

    private static void applyQuartz(ItemStack tool, boolean mythic) {
        int count = mythic ? 72 : 36; // 72 = one full tier
        ModifyBuilder.instance.modifyItem(tool, new ItemStack[] { new ItemStack(Items.quartz, count) });
    }

    private static void applyLapis(ItemStack tool, boolean mythic) {
        int count = mythic ? 450 : 200; // 450 = fortune/looting III
        ModifyBuilder.instance.modifyItem(tool, new ItemStack[] { new ItemStack(Items.dye, count, 4) });
    }

    private static void applyRedstone(ItemStack tool, boolean mythic) {
        int count = mythic ? 50 : 25;
        ModifyBuilder.instance.modifyItem(tool, new ItemStack[] { new ItemStack(Items.redstone, count) });
    }
}
