package com.vonbraunz.apogtnh.affix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import com.vonbraunz.apogtnh.ApoConfig;
import com.vonbraunz.apogtnh.placebo.PlaceboUtil;

/**
 * NBT layout for affix data on an ItemStack:
 *
 * root:
 * apogtnh:
 * rarity: STRING (LootRarity name)
 * affixes: COMPOUND {
 * <affix_id_1>: INT (level)
 * <affix_id_2>: INT (level)
 * }
 *
 * All read/write goes through here to keep the layout consistent.
 */
public class AffixHelper {

    public static final String ROOT = "apogtnh";
    public static final String KEY_RARITY = "rarity";
    public static final String KEY_AFFIXES = "affixes";

    // ---- Read ---------------------------------------------------------------

    public static boolean hasAffixData(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) return false;
        return stack.getTagCompound()
            .hasKey(ROOT, 10);
    }

    public static LootRarity getRarity(ItemStack stack) {
        if (!hasAffixData(stack)) return null;
        NBTTagCompound apo = stack.getTagCompound()
            .getCompoundTag(ROOT);
        String name = apo.getString(KEY_RARITY);
        try {
            return LootRarity.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** Returns a map of Affix → level for all affixes on the item. Never null. */
    public static Map<Affix, Integer> getAffixes(ItemStack stack) {
        Map<Affix, Integer> out = new HashMap<Affix, Integer>();
        if (!hasAffixData(stack)) return out;
        NBTTagCompound apo = stack.getTagCompound()
            .getCompoundTag(ROOT);
        if (!apo.hasKey(KEY_AFFIXES, 10)) return out;
        NBTTagCompound affixes = apo.getCompoundTag(KEY_AFFIXES);
        // NOTE: 1.7.10 MCP left NBTTagCompound.getKeySet unmapped as func_150296_c.
        // GTNH's mapping fork may provide a friendly name; verify and swap if so.
        for (Object keyObj : affixes.func_150296_c()) { // getKeySet
            String id = (String) keyObj;
            Affix a = AffixRegistry.get(id);
            if (a != null) out.put(a, affixes.getInteger(id));
        }
        return out;
    }

    // ---- Write --------------------------------------------------------------

    public static void applyRoll(ItemStack stack, LootRarity rarity, Random rand) {
        LootCategory cat = LootCategory.forStack(stack);
        if (cat == null) return;

        int count = Math.min(rarity.affixCount, ApoConfig.maxAffixesPerItem);
        List<Affix> picked = AffixRegistry.pickDistinct(rand, cat, count);
        if (picked.isEmpty()) return;

        NBTTagCompound root = PlaceboUtil.getStackNBT(stack);
        NBTTagCompound apo = PlaceboUtil.getOrCreateSub(root, ROOT);
        apo.setString(KEY_RARITY, rarity.name());

        NBTTagCompound affixes = new NBTTagCompound();
        for (Affix a : picked) {
            affixes.setInteger(a.id, a.rollLevel(rand, rarity));
        }
        apo.setTag(KEY_AFFIXES, affixes);

        applyDisplayName(stack, rarity, picked);
    }

    /** Wipe all affix NBT for a full reroll. Reforging table calls this then applyRoll. */
    public static void clearAffixData(ItemStack stack) {
        if (stack != null && stack.hasTagCompound()) {
            stack.getTagCompound()
                .removeTag(ROOT);
        }
        // Also clear the display name if we set one
        if (stack != null && stack.hasTagCompound()
            && stack.getTagCompound()
                .hasKey("display", 10)) {
            NBTTagCompound display = stack.getTagCompound()
                .getCompoundTag("display");
            display.removeTag("Name");
        }
    }

    // exposed for ReforgeController.upgrade to rebuild the name after swapping one affix
    public static void applyDisplayName(ItemStack stack, LootRarity rarity, List<Affix> picked) {
        // Simple naming: "<Rarity color> [Affix0] [BaseName] [Affix-1]"
        // Modern Apotheosis has a richer name template; keep this minimal for now.
        String prefix = picked.isEmpty() ? ""
            : picked.get(0)
                .displayName(1) + " ";
        String suffix = picked.size() > 1 ? " of " + picked.get(picked.size() - 1)
            .displayName(1) : "";
        String base = stack.getItem()
            .getItemStackDisplayName(stack);
        String full = rarity.color + prefix + base + suffix + EnumChatFormatting.RESET;
        stack.setStackDisplayName(full);
    }

    // ---- Tooltip ------------------------------------------------------------

    public static List<String> buildTooltipLines(ItemStack stack) {
        List<String> lines = new ArrayList<String>();
        LootRarity rarity = getRarity(stack);
        if (rarity == null) return lines;
        lines.add(rarity.color + rarity.name());
        for (Map.Entry<Affix, Integer> e : getAffixes(stack).entrySet()) {
            String line = e.getKey()
                .tooltip(stack, e.getValue());
            if (line != null) lines.add(EnumChatFormatting.GRAY + line);
        }
        return lines;
    }
}
