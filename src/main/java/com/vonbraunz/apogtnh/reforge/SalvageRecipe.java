package com.vonbraunz.apogtnh.reforge;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import com.vonbraunz.apogtnh.ApoConfig;
import com.vonbraunz.apogtnh.ModContent;
import com.vonbraunz.apogtnh.affix.AffixHelper;
import com.vonbraunz.apogtnh.affix.LootRarity;
import com.vonbraunz.apogtnh.reforge.item.ItemSalvageSigil;

/**
 * Shapeless crafting-table recipe: Salvaging Hammer + any affixed item -> rarity material
 * at the item's rarity. The hammer takes durability damage via {@link ItemSalvageSigil}'s
 * container-item mechanic (same pattern as vanilla buckets leaving empty buckets in the
 * grid). No anvil, no smithing table, no GUI -- just the 2x2 or 3x3 crafting grid.
 */
public class SalvageRecipe implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        int hammerCount = 0;
        int affixCount = 0;
        int otherCount = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack == null) continue;
            if (stack.getItem() instanceof ItemSalvageSigil) {
                hammerCount++;
            } else if (AffixHelper.hasAffixData(stack)) {
                affixCount++;
            } else {
                otherCount++;
            }
        }
        // exactly one hammer, exactly one affixed item, nothing else
        return hammerCount == 1 && affixCount == 1 && otherCount == 0;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null && AffixHelper.hasAffixData(stack)) {
                LootRarity rarity = AffixHelper.getRarity(stack);
                if (rarity == null) break;
                return new ItemStack(ModContent.rarityMaterial, ApoConfig.salvageMaterialYield, rarity.ordinal());
            }
        }
        return null;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        // sample output for NEI display -- common material as placeholder
        return new ItemStack(ModContent.rarityMaterial, 1, 0);
    }
}
