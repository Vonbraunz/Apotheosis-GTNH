package com.vonbraunz.apogtnh;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.vonbraunz.apogtnh.reforge.SalvageRecipe;
import com.vonbraunz.apogtnh.reforge.item.ItemAffixBook;
import com.vonbraunz.apogtnh.reforge.item.ItemAugmentCrystal;
import com.vonbraunz.apogtnh.reforge.item.ItemPolishingCrystal;
import com.vonbraunz.apogtnh.reforge.item.ItemRarityMaterial;
import com.vonbraunz.apogtnh.reforge.item.ItemSalvageSigil;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Central registration point for items + recipes. Refoging and augmenting run through the
 * vanilla anvil (AnvilHandler); salvaging runs through the vanilla crafting table
 * (SalvageRecipe + ItemSalvageSigil container-item durability).
 */
public class ModContent {

    public static Item rarityMaterial;
    public static Item augmentCrystal;
    public static Item polishingCrystal;
    public static Item salvageSigil;
    public static Item affixBook;

    public static void preInit() {
        rarityMaterial = new ItemRarityMaterial().setUnlocalizedName("apogtnh:rarity_material")
            .setCreativeTab(ApotheosisGTNH.creativeTab);
        augmentCrystal = new ItemAugmentCrystal().setUnlocalizedName("apogtnh:augment_crystal")
            .setCreativeTab(ApotheosisGTNH.creativeTab);
        polishingCrystal = new ItemPolishingCrystal().setUnlocalizedName("apogtnh:polishing_crystal")
            .setCreativeTab(ApotheosisGTNH.creativeTab);
        salvageSigil = new ItemSalvageSigil().setUnlocalizedName("apogtnh:salvage_sigil")
            .setCreativeTab(ApotheosisGTNH.creativeTab);
        affixBook = new ItemAffixBook().setUnlocalizedName("apogtnh:affix_book")
            .setCreativeTab(ApotheosisGTNH.creativeTab);

        GameRegistry.registerItem(rarityMaterial, "rarityMaterial");
        GameRegistry.registerItem(augmentCrystal, "augmentCrystal");
        GameRegistry.registerItem(polishingCrystal, "polishingCrystal");
        GameRegistry.registerItem(salvageSigil, "salvageSigil");
        GameRegistry.registerItem(affixBook, "affixBook");

        registerRecipes();
    }

    private static void registerRecipes() {
        // Salvaging Hammer: obsidian head + stick handle, yields 1.
        GameRegistry
            .addRecipe(new ItemStack(salvageSigil), "OOO", " S ", " S ", 'O', Blocks.obsidian, 'S', Items.stick);

        // Salvaging: hammer + affixed item in crafting grid -> rarity material.
        GameRegistry.addRecipe(new SalvageRecipe());

        // Augment Crystal: diamond center, glowstone corners, glass sides, yields 1.
        GameRegistry.addRecipe(
            new ItemStack(augmentCrystal),
            "GWG",
            "WDW",
            "GWG",
            'G',
            Items.glowstone_dust,
            'W',
            Blocks.glass,
            'D',
            Items.diamond);

        // Polishing Crystal: nether brick border, diamond corners, ender pearl center, yields 1.
        GameRegistry.addRecipe(
            new ItemStack(polishingCrystal),
            "NDN",
            "DED",
            "NDN",
            'N',
            Items.netherbrick,
            'D',
            Items.diamond,
            'E',
            Items.ender_pearl);
    }
}
