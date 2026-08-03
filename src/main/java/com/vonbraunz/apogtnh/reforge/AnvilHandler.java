package com.vonbraunz.apogtnh.reforge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;

import com.vonbraunz.apogtnh.ApoConfig;
import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.AffixHelper;
import com.vonbraunz.apogtnh.affix.LootCategory;
import com.vonbraunz.apogtnh.affix.LootRarity;
import com.vonbraunz.apogtnh.reforge.item.ItemAugmentCrystal;
import com.vonbraunz.apogtnh.reforge.item.ItemRarityMaterial;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Every anvil-based mechanic in the mod, distinguished entirely by the right-slot item --
 * which item you insert *is* the choice of mechanic, so there's no ambiguity between them:
 *
 * - ItemRarityMaterial -> Reforging: full reroll. Wipes existing affixes (if any) and
 * rolls a fresh set at the material's rarity tier. Works on any affix-eligible item,
 * affixed or not.
 * - ItemAugmentCrystal -> Augmenting: rerolls one random affix already on the item, keeps
 * the rest + the item's current rarity. Requires the item to already have affix data.
 * - ItemSalvageSigil -> Salvaging: destroys an affixed item, returns a rarity material at
 * its current rarity. The economy loop that lets you actually obtain rarity materials
 * without /give -- salvage items you don't want, spend the material to reforge ones you
 * do.
 *
 * All three used to be (or were considered as) separate custom blocks/GUIs. All three hit
 * the same problem -- hand-copied modern-Apotheosis textures whose baked-in slot outlines
 * never matched hardcoded slot coordinates. Routing everything through the vanilla anvil
 * sidesteps that entirely: no custom texture, no custom slot layout, ever.
 *
 * One handler, not three independent AnvilUpdateEvent subscribers, so there's no ordering
 * ambiguity about who gets to set event.output. Scaffold only for reforge/augment --
 * ReforgeController is still stubbed, so those two branches don't change the output yet.
 * Salvage is a straight lookup (no ReforgeController involvement) and is fully implemented.
 */
public class AnvilHandler {

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.left;
        ItemStack right = event.right;
        if (left == null || right == null) return;

        if (right.getItem() instanceof ItemRarityMaterial) {
            handleReforge(event, left, right);
        } else if (right.getItem() instanceof ItemAugmentCrystal) {
            handleAugment(event, left, right);
        }
    }

    private void handleReforge(AnvilUpdateEvent event, ItemStack left, ItemStack right) {
        if (!ApoConfig.enableReforging) return;
        if (LootCategory.forStack(left) == null) return;

        LootRarity rarity = ItemRarityMaterial.rarityFromStack(right);
        if (rarity == null) return;
        if (right.stackSize < ReforgeCost.material(rarity)) return;

        // deterministic seed so client preview and server result match
        Random rand = new Random(seed(left, right));
        ItemStack result = ReforgeController.rollPreview(rand, left, rarity);

        event.output = result;
        event.cost = ReforgeCost.xpLevels(rarity);
        event.materialCost = ReforgeCost.material(rarity);
    }

    private void handleAugment(AnvilUpdateEvent event, ItemStack left, ItemStack right) {
        if (!ApoConfig.enableAugmenting) return;
        if (!AffixHelper.hasAffixData(left)) return;
        if (right.stackSize < ApoConfig.augmentMaterialCost) return;

        Map<Affix, Integer> current = AffixHelper.getAffixes(left);
        if (current.isEmpty()) return;

        Random rand = new Random(seed(left, right));
        Affix target = pickRandomAffix(rand, current);

        ItemStack result = left.copy();
        List<Affix> alternatives = ReforgeController.getAlternativeAffixes(result, target);
        if (alternatives == null || alternatives.isEmpty()) return;
        Affix chosen = alternatives.get(rand.nextInt(alternatives.size()));
        int newLevel = chosen.rollLevel(rand);
        ReforgeController.upgrade(result, target, chosen, newLevel);

        event.output = result;
        event.cost = ApoConfig.augmentLevelCost;
        event.materialCost = ApoConfig.augmentMaterialCost;
    }

    private Affix pickRandomAffix(Random rand, Map<Affix, Integer> affixes) {
        List<Affix> keys = new ArrayList<Affix>(affixes.keySet());
        return keys.get(rand.nextInt(keys.size()));
    }

    /**
     * Deterministic seed based on the two input stacks so client preview and server
     * result always produce the same random roll. Without this, {@code new Random()}
     * diverges on each side and the anvil shows one item but gives another.
     */
    private static long seed(ItemStack left, ItemStack right) {
        long s = left.getItem()
            .hashCode();
        s = s * 31 + left.getItemDamage();
        if (left.hasTagCompound()) s = s * 31 + left.getTagCompound()
            .hashCode();
        s = s * 31 + right.getItem()
            .hashCode();
        s = s * 31 + right.getItemDamage();
        return s;
    }
}
