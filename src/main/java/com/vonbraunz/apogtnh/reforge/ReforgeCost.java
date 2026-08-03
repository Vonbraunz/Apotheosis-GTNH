package com.vonbraunz.apogtnh.reforge;

import com.vonbraunz.apogtnh.ApoConfig;
import com.vonbraunz.apogtnh.affix.LootRarity;

/** Thin accessor over the per-rarity reforge/augment cost fields in ApoConfig. */
public class ReforgeCost {

    public static int material(LootRarity rarity) {
        switch (rarity) {
            case COMMON:
                return ApoConfig.reforgeMaterialCostCommon;
            case UNCOMMON:
                return ApoConfig.reforgeMaterialCostUncommon;
            case RARE:
                return ApoConfig.reforgeMaterialCostRare;
            case EPIC:
                return ApoConfig.reforgeMaterialCostEpic;
            case MYTHIC:
                return ApoConfig.reforgeMaterialCostMythic;
            default:
                return 0;
        }
    }

    public static int xpLevels(LootRarity rarity) {
        switch (rarity) {
            case COMMON:
                return ApoConfig.reforgeXpLevelCostCommon;
            case UNCOMMON:
                return ApoConfig.reforgeXpLevelCostUncommon;
            case RARE:
                return ApoConfig.reforgeXpLevelCostRare;
            case EPIC:
                return ApoConfig.reforgeXpLevelCostEpic;
            case MYTHIC:
                return ApoConfig.reforgeXpLevelCostMythic;
            default:
                return 0;
        }
    }
}
