package com.vonbraunz.apogtnh;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

/**
 * Central config. All tuning values live here so you can adjust without recompiling.
 * Every drop rate is expressed as "1-in-N" where N is the config value; N=0 disables.
 */
public class ApoConfig {

    // Spawn tags (all 1-in-N on natural hostile spawn)
    public static int bossSpawnChance = 400; // 1-in-400 hostile spawns become bosses. 0 disables.
    public static int eliteSpawnChance = 50; // 1-in-50 hostile spawns become elites
    public static int carrierSpawnChance = 500; // 1-in-500 hostile spawns become carriers

    // Drop chances by tier (percent)
    public static int bossDropPercent = 100;
    public static int eliteDropPercent = 100; // TODO: revert to 50 after verifying drops work
    public static int carrierDropPercent = 100;

    // Regular (untagged) mob drop chance: 1-in-N chance to drop a low-rarity affix item
    public static int mobDropChance = 50; // 1-in-50 regular mobs drop an affix. 0 disables.
    public static int mobDropPercent = 50; // percent chance to actually get the drop when the roll hits

    // Restrict carrier drops to player kills to avoid mob-farm faucets
    public static boolean carrierRequirePlayerKill = true;

    // Affix roll settings
    public static int maxAffixesPerItem = 3;

    // Master toggles
    public static boolean enableBossSpawns = true;
    public static boolean enableEliteSpawns = true;
    public static boolean enableCarrierDrops = true;
    public static boolean enableMobDrops = true;

    // Reforge / augment master toggles -- both run through AnvilHandler
    public static boolean enableReforging = true;
    public static boolean enableAugmenting = true;

    // Per-rarity reforge costs (rarity-material item count / XP levels), scaffold defaults
    public static int reforgeMaterialCostCommon = 1;
    public static int reforgeMaterialCostUncommon = 2;
    public static int reforgeMaterialCostRare = 2;
    public static int reforgeMaterialCostEpic = 3;
    public static int reforgeMaterialCostMythic = 3;

    public static int reforgeXpLevelCostCommon = 3;
    public static int reforgeXpLevelCostUncommon = 6;
    public static int reforgeXpLevelCostRare = 15;
    public static int reforgeXpLevelCostEpic = 25;
    // vanilla anvil caps at 39 levels -- 40 would show "Too Expensive!"
    public static int reforgeXpLevelCostMythic = 39;

    // Single-affix augment cost (swap)
    public static int augmentMaterialCost = 1;
    public static int augmentLevelCost = 5;

    // Single-affix upgrade cost (polish / +1 level)
    public static int upgradeMaterialCost = 1;
    public static int upgradeLevelCost = 5;

    // Salvage yield (crafting table, SalvageRecipe)
    public static int salvageMaterialYield = 1;

    private static Configuration config;

    public static void load(File file) {
        config = new Configuration(file);
        try {
            config.load();

            bossSpawnChance = config.getInt(
                "bossSpawnChance",
                "spawns",
                bossSpawnChance,
                0,
                10000,
                "1-in-N chance a hostile spawn becomes a boss. 0 disables.");
            eliteSpawnChance = config.getInt(
                "eliteSpawnChance",
                "spawns",
                eliteSpawnChance,
                0,
                10000,
                "1-in-N chance a hostile spawn becomes elite. 0 disables.");
            carrierSpawnChance = config.getInt(
                "carrierSpawnChance",
                "spawns",
                carrierSpawnChance,
                0,
                10000,
                "1-in-N chance a hostile spawn is a silent affix carrier. 0 disables.");

            bossDropPercent = config
                .getInt("bossDropPercent", "drops", bossDropPercent, 0, 100, "Percent chance a boss drops affix loot.");
            eliteDropPercent = config.getInt(
                "eliteDropPercent",
                "drops",
                eliteDropPercent,
                0,
                100,
                "Percent chance an elite drops affix loot.");
            carrierDropPercent = config.getInt(
                "carrierDropPercent",
                "drops",
                carrierDropPercent,
                0,
                100,
                "Percent chance a carrier drops affix loot.");
            mobDropChance = config.getInt(
                "mobDropChance",
                "drops",
                mobDropChance,
                0,
                10000,
                "1-in-N chance a regular untagged hostile mob drops an affix item. 0 disables.");
            mobDropPercent = config.getInt(
                "mobDropPercent",
                "drops",
                mobDropPercent,
                0,
                100,
                "Percent chance the drop actually fires when the 1-in-N roll hits.");
            carrierRequirePlayerKill = config.getBoolean(
                "carrierRequirePlayerKill",
                "drops",
                carrierRequirePlayerKill,
                "Require player kill for carrier drops (blocks mob farm faucets).");

            maxAffixesPerItem = config.getInt(
                "maxAffixesPerItem",
                "affix",
                maxAffixesPerItem,
                1,
                8,
                "Cap on affixes rolled onto a single item.");

            enableBossSpawns = config
                .getBoolean("enableBossSpawns", "toggle", enableBossSpawns, "Master toggle for boss spawns.");
            enableEliteSpawns = config
                .getBoolean("enableEliteSpawns", "toggle", enableEliteSpawns, "Master toggle for elite spawns.");
            enableCarrierDrops = config.getBoolean(
                "enableCarrierDrops",
                "toggle",
                enableCarrierDrops,
                "Master toggle for silent carrier drops.");
            enableMobDrops = config
                .getBoolean("enableMobDrops", "toggle", enableMobDrops, "Master toggle for regular mob affix drops.");

            enableReforging = config.getBoolean(
                "enableReforging",
                "reforge",
                enableReforging,
                "Master toggle for anvil-based reforging (AnvilHandler).");
            enableAugmenting = config.getBoolean(
                "enableAugmenting",
                "reforge",
                enableAugmenting,
                "Master toggle for anvil-based augmenting (AnvilHandler).");

            reforgeMaterialCostCommon = config.getInt(
                "reforgeMaterialCostCommon",
                "reforge",
                reforgeMaterialCostCommon,
                0,
                64,
                "Rarity material cost to reforge at Common.");
            reforgeMaterialCostUncommon = config.getInt(
                "reforgeMaterialCostUncommon",
                "reforge",
                reforgeMaterialCostUncommon,
                0,
                64,
                "Rarity material cost to reforge at Uncommon.");
            reforgeMaterialCostRare = config.getInt(
                "reforgeMaterialCostRare",
                "reforge",
                reforgeMaterialCostRare,
                0,
                64,
                "Rarity material cost to reforge at Rare.");
            reforgeMaterialCostEpic = config.getInt(
                "reforgeMaterialCostEpic",
                "reforge",
                reforgeMaterialCostEpic,
                0,
                64,
                "Rarity material cost to reforge at Epic.");
            reforgeMaterialCostMythic = config.getInt(
                "reforgeMaterialCostMythic",
                "reforge",
                reforgeMaterialCostMythic,
                0,
                64,
                "Rarity material cost to reforge at Mythic.");

            reforgeXpLevelCostCommon = config.getInt(
                "reforgeXpLevelCostCommon",
                "reforge",
                reforgeXpLevelCostCommon,
                0,
                1000,
                "XP level cost to reforge at Common.");
            reforgeXpLevelCostUncommon = config.getInt(
                "reforgeXpLevelCostUncommon",
                "reforge",
                reforgeXpLevelCostUncommon,
                0,
                1000,
                "XP level cost to reforge at Uncommon.");
            reforgeXpLevelCostRare = config.getInt(
                "reforgeXpLevelCostRare",
                "reforge",
                reforgeXpLevelCostRare,
                0,
                1000,
                "XP level cost to reforge at Rare.");
            reforgeXpLevelCostEpic = config.getInt(
                "reforgeXpLevelCostEpic",
                "reforge",
                reforgeXpLevelCostEpic,
                0,
                1000,
                "XP level cost to reforge at Epic.");
            reforgeXpLevelCostMythic = config.getInt(
                "reforgeXpLevelCostMythic",
                "reforge",
                reforgeXpLevelCostMythic,
                0,
                39, // vanilla anvil max is 39 before "Too Expensive!"
                "XP level cost to reforge at Mythic.");

            augmentMaterialCost = config.getInt(
                "augmentMaterialCost",
                "reforge",
                augmentMaterialCost,
                0,
                64,
                "Rarity material count (anvil right slot) to reroll a single affix; must match the item's own rarity.");
            augmentLevelCost = config.getInt(
                "augmentLevelCost",
                "reforge",
                augmentLevelCost,
                0,
                1000,
                "XP level cost (anvil) to reroll a single affix.");

            upgradeMaterialCost = config.getInt(
                "upgradeMaterialCost",
                "reforge",
                upgradeMaterialCost,
                0,
                64,
                "Polishing Crystal count consumed to upgrade one affix by 1 level.");
            upgradeLevelCost = config.getInt(
                "upgradeLevelCost",
                "reforge",
                upgradeLevelCost,
                0,
                1000,
                "XP level cost (anvil) to upgrade one affix by 1 level.");

            salvageMaterialYield = config.getInt(
                "salvageMaterialYield",
                "reforge",
                salvageMaterialYield,
                1,
                64,
                "Rarity material count produced per salvage, at the salvaged item's rarity.");
        } finally {
            if (config.hasChanged()) config.save();
        }
    }
}
