package com.vonbraunz.apogtnh.reforge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.AnvilUpdateEvent;

import com.vonbraunz.apogtnh.ApoConfig;
import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.AffixHelper;
import com.vonbraunz.apogtnh.affix.LootCategory;
import com.vonbraunz.apogtnh.affix.LootRarity;
import com.vonbraunz.apogtnh.reforge.item.ItemAugmentCrystal;
import com.vonbraunz.apogtnh.reforge.item.ItemPolishingCrystal;
import com.vonbraunz.apogtnh.reforge.item.ItemRarityMaterial;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * The two anvil-based mechanics in the mod, distinguished entirely by the right-slot item
 * -- which item you insert *is* the choice of mechanic, so there's no ambiguity between
 * them:
 *
 * - ItemRarityMaterial -> Reforging: full reroll. Wipes existing affixes (if any) and
 * rolls a fresh set at the material's rarity tier. Works on any affix-eligible item,
 * affixed or not.
 * - ItemAugmentCrystal -> Augmenting: rerolls one random affix already on the item, keeps
 * the rest + the item's current rarity. Requires the item to already have affix data.
 *
 * Salvaging (affixed item -> rarity material, the obtain path for ItemRarityMaterial) is
 * NOT here -- it runs through the vanilla crafting table instead, via SalvageRecipe +
 * ItemSalvageSigil's durability-based container-item mechanic. Anvil didn't fit it as well
 * once salvage stopped needing a "which rarity do I want" choice the way reforge does.
 *
 * Both mechanics here used to be (or were considered as) separate custom blocks/GUIs. Both
 * hit the same problem -- hand-copied modern-Apotheosis textures whose baked-in slot
 * outlines never matched hardcoded slot coordinates. Routing them through the vanilla
 * anvil sidesteps that entirely: no custom texture, no custom slot layout, ever.
 *
 * One handler, not two independent AnvilUpdateEvent subscribers, so there's no ordering
 * ambiguity about who gets to set event.output.
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
        } else if (right.getItem() instanceof ItemPolishingCrystal) {
            handleUpgrade(event, left, right);
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
        if (right.stackSize < 1) return;

        // stack count selects which affix to target: 1 crystal = first affix, 2 = second, etc.
        // more crystals than affixes on the item -> reject, no fallback to random.
        List<Affix> ordered = sortedAffixes(left);
        if (ordered.isEmpty()) return;

        int index = right.stackSize - 1;
        if (index >= ordered.size()) return;
        Affix target = ordered.get(index);

        Random rand = new Random(seed(left, right));
        ItemStack result = left.copy();
        List<Affix> alternatives = ReforgeController.getAlternativeAffixes(result, target);
        if (alternatives == null || alternatives.isEmpty()) return;
        Affix chosen = alternatives.get(rand.nextInt(alternatives.size()));
        int newLevel = chosen.rollLevel(rand);
        ReforgeController.upgrade(result, target, chosen, newLevel);

        event.output = result;
        event.cost = ApoConfig.augmentLevelCost;
        event.materialCost = right.stackSize;
    }

    private void handleUpgrade(AnvilUpdateEvent event, ItemStack left, ItemStack right) {
        if (!AffixHelper.hasAffixData(left)) return;
        List<Affix> ordered = sortedAffixes(left);
        if (ordered.isEmpty()) return;

        int index = right.stackSize - 1;
        if (index >= ordered.size()) return;
        Affix target = ordered.get(index);

        Map<Affix, Integer> current = AffixHelper.getAffixes(left);
        int currentLevel = current.get(target);
        if (currentLevel >= target.maxLevel) return; // already maxed

        ItemStack result = left.copy();
        // NBT surgery: bump the affix level by 1, leave everything else alone
        NBTTagCompound root = result.getTagCompound();
        NBTTagCompound apo = root.getCompoundTag(AffixHelper.ROOT);
        NBTTagCompound affixes = apo.getCompoundTag(AffixHelper.KEY_AFFIXES);
        affixes.setInteger(target.id, currentLevel + 1);

        // rebuild display name
        if (root.hasKey("display", 10)) {
            root.getCompoundTag("display")
                .removeTag("Name");
        }
        LootRarity rarity = AffixHelper.getRarity(result);
        List<Affix> affixList = new ArrayList<Affix>(
            AffixHelper.getAffixes(result)
                .keySet());
        AffixHelper.applyDisplayName(result, rarity, affixList);

        event.output = result;
        event.cost = ApoConfig.upgradeLevelCost;
        event.materialCost = right.stackSize;
    }

    /** affixes sorted by id so the order is deterministic and documented. */
    private static List<Affix> sortedAffixes(ItemStack stack) {
        List<Affix> list = new ArrayList<Affix>(
            AffixHelper.getAffixes(stack)
                .keySet());
        java.util.Collections.sort(list, new java.util.Comparator<Affix>() {

            @Override
            public int compare(Affix a, Affix b) {
                return a.id.compareTo(b.id);
            }
        });
        return list;
    }

    private Affix pickRandomAffix(Random rand, Map<Affix, Integer> affixes) {
        List<Affix> keys = new ArrayList<Affix>(affixes.keySet());
        return keys.get(rand.nextInt(keys.size()));
    }

    /**
     * Deterministic seed based on the two input stacks so client preview and server
     * result always produce the same random roll. Without this, {@code new Random()}
     * diverges on each side and the anvil shows one item but gives another.
     *
     * Built from value-based content (rarity name, sorted affix id/level pairs) rather
     * than {@code left.getTagCompound().hashCode()} -- NBTTagCompound doesn't override
     * hashCode()/equals() in 1.7.10, so that would fall back to Object identity and differ
     * between the client's and server's separately-deserialized copies of the same item,
     * defeating the whole point of a shared seed.
     */
    private static long seed(ItemStack left, ItemStack right) {
        long s = left.getItem()
            .hashCode();
        s = s * 31 + left.getItemDamage();
        s = s * 31 + affixContentHash(left);
        s = s * 31 + right.getItem()
            .hashCode();
        s = s * 31 + right.getItemDamage();
        return s;
    }

    private static long affixContentHash(ItemStack stack) {
        LootRarity rarity = AffixHelper.getRarity(stack);
        if (rarity == null) return 0L;

        long h = rarity.name()
            .hashCode();

        List<Map.Entry<Affix, Integer>> entries = new ArrayList<Map.Entry<Affix, Integer>>(
            AffixHelper.getAffixes(stack)
                .entrySet());
        // stable order required -- two independently-populated HashMaps with the same
        // entries are not guaranteed to iterate in the same order
        Collections.sort(entries, new Comparator<Map.Entry<Affix, Integer>>() {

            @Override
            public int compare(Map.Entry<Affix, Integer> a, Map.Entry<Affix, Integer> b) {
                return a.getKey().id.compareTo(b.getKey().id);
            }
        });

        for (Map.Entry<Affix, Integer> e : entries) {
            h = h * 31 + e.getKey().id.hashCode();
            h = h * 31 + e.getValue();
        }
        return h;
    }
}
