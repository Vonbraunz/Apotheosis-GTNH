package com.vonbraunz.apogtnh.affix;

import net.minecraft.util.EnumChatFormatting;

public enum LootRarity {

    COMMON(EnumChatFormatting.WHITE, 1, 200),
    UNCOMMON(EnumChatFormatting.YELLOW, 2, 100),
    RARE(EnumChatFormatting.AQUA, 2, 40),
    EPIC(EnumChatFormatting.LIGHT_PURPLE, 3, 10),
    MYTHIC(EnumChatFormatting.GOLD, 3, 2);

    public final EnumChatFormatting color;
    /** Number of affixes rolled at this rarity. */
    public final int affixCount;
    /** Relative weight when drawing a random rarity. */
    public final int weight;

    LootRarity(EnumChatFormatting color, int affixCount, int weight) {
        this.color = color;
        this.affixCount = affixCount;
        this.weight = weight;
    }

    /** Random pick weighted by `weight`. Used at drop-time. */
    public static LootRarity roll(java.util.Random rand) {
        int total = 0;
        for (LootRarity r : values()) total += r.weight;
        int pick = rand.nextInt(total);
        int acc = 0;
        for (LootRarity r : values()) {
            acc += r.weight;
            if (pick < acc) return r;
        }
        return COMMON;
    }

    /** Random pick capped at a maximum rarity (e.g. carrier tier caps at UNCOMMON). */
    public static LootRarity rollCapped(java.util.Random rand, LootRarity max) {
        int total = 0;
        for (LootRarity r : values()) {
            if (r.ordinal() <= max.ordinal()) total += r.weight;
        }
        int pick = rand.nextInt(total);
        int acc = 0;
        for (LootRarity r : values()) {
            if (r.ordinal() > max.ordinal()) break;
            acc += r.weight;
            if (pick < acc) return r;
        }
        return COMMON;
    }
}
