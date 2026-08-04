package com.vonbraunz.apogtnh.reforge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.AffixHelper;
import com.vonbraunz.apogtnh.affix.AffixRegistry;
import com.vonbraunz.apogtnh.affix.LootCategory;
import com.vonbraunz.apogtnh.affix.LootRarity;

/**
 * Shared reroll logic used by both reforging (full reroll) and augmenting
 * (single-affix reroll), now routed through the vanilla anvil.
 */
public class ReforgeController {

    /**
     * Full reforge: wipes existing affix data on a copy of the input and rolls a
     * fresh set at the given rarity.
     */
    public static ItemStack rollPreview(Random rand, ItemStack input, LootRarity rarity) {
        ItemStack result = input.copy();
        AffixHelper.clearAffixData(result);
        AffixHelper.applyRoll(result, rarity, rand);
        return result;
    }

    /**
     * Candidate affixes eligible to replace {@code current} on {@code stack}.
     * Excludes the current affix and any affixes already present on the item so
     * the pool only contains fresh alternatives.
     */
    public static List<Affix> getAlternativeAffixes(ItemStack stack, Affix current) {
        LootCategory cat = LootCategory.forStack(stack);
        if (cat == null) return null;

        List<Affix> pool = new ArrayList<Affix>(AffixRegistry.forCategory(cat));
        Map<Affix, Integer> existing = AffixHelper.getAffixes(stack);

        // remove any affix already present on the item (including current)
        pool.removeAll(existing.keySet());

        return pool.isEmpty() ? null : pool;
    }

    /**
     * Replace {@code oldAffix} with {@code newAffix} at {@code newLevel} on
     * {@code stack}, leaving other affixes and the item's rarity untouched.
     * Rebuilds the display name afterward.
     */
    public static void upgrade(ItemStack stack, Affix oldAffix, Affix newAffix, int newLevel) {
        if (!AffixHelper.hasAffixData(stack)) return;

        NBTTagCompound root = stack.getTagCompound();
        NBTTagCompound apo = root.getCompoundTag(AffixHelper.ROOT);
        NBTTagCompound affixes = apo.getCompoundTag(AffixHelper.KEY_AFFIXES);

        // swap the affix key
        affixes.removeTag(oldAffix.id);
        affixes.setInteger(newAffix.id, newLevel);

        // nuke the old display name so the rebuild starts from the base item name
        if (root.hasKey("display", 10)) {
            root.getCompoundTag("display")
                .removeTag("Name");
        }

        // rebuild display name from the updated affix set
        LootRarity rarity = AffixHelper.getRarity(stack);
        List<Affix> affixList = new ArrayList<Affix>(
            AffixHelper.getAffixes(stack)
                .keySet());
        AffixHelper.applyDisplayName(stack, rarity, affixList);
    }
}
