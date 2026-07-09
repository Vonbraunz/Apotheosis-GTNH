package com.vonbraunz.apogtnh.affix;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

import tconstruct.items.tools.BowBase;
import tconstruct.library.tools.HarvestTool;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.tools.Weapon;
import tconstruct.library.util.IToolPart;

/** What category of item an affix can roll onto. Used for pool filtering. */
public enum LootCategory {

    SWORD,
    RANGED,
    TOOL, // pickaxe/axe/shovel/hoe/shears
    HELMET,
    CHESTPLATE,
    LEGGINGS,
    BOOTS,
    ANY; // usable on all

    public static LootCategory forStack(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return null;
        Item item = stack.getItem();

        // Tinkers' Construct tools (check before vanilla since they don't extend vanilla classes)
        if (item instanceof Weapon) return SWORD;
        if (item instanceof BowBase) return RANGED;
        if (item instanceof HarvestTool || item instanceof ToolCore) return TOOL;

        // Tinkers' tool parts -- classify by part type name
        if (item instanceof IToolPart) {
            String name = item.getUnlocalizedName()
                .toLowerCase();
            if (name.contains("sword") || name.contains("blade")) return SWORD;
            if (name.contains("bow")) return RANGED;
            return TOOL; // pickaxe head, shovel head, axe head, etc.
        }

        if (item instanceof ItemSword) return SWORD;
        if (item instanceof ItemBow) return RANGED;
        if (item instanceof ItemPickaxe || item instanceof ItemAxe
            || item instanceof ItemSpade
            || item instanceof ItemHoe
            || item instanceof ItemShears
            || item instanceof ItemTool) return TOOL;

        if (item instanceof ItemArmor) {
            switch (((ItemArmor) item).armorType) {
                case 0:
                    return HELMET;
                case 1:
                    return CHESTPLATE;
                case 2:
                    return LEGGINGS;
                case 3:
                    return BOOTS;
                default:
                    return null;
            }
        }
        return null;
    }
}
