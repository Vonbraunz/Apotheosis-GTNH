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
    public static int eliteDropPercent = 50;
    public static int carrierDropPercent = 100;

    // Regular (untagged) mob drop chance: 1-in-N chance to drop a low-rarity affix item
    public static int mobDropChance = 200; // 1-in-200 regular mobs drop an affix. 0 disables.
    public static int mobDropPercent = 30; // percent chance to actually get the drop when the roll hits

    // Restrict carrier drops to player kills to avoid mob-farm faucets
    public static boolean carrierRequirePlayerKill = true;

    // Affix roll settings
    public static int maxAffixesPerItem = 3;

    // Master toggles
    public static boolean enableBossSpawns = true;
    public static boolean enableEliteSpawns = true;
    public static boolean enableCarrierDrops = true;
    public static boolean enableMobDrops = true;

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
        } finally {
            if (config.hasChanged()) config.save();
        }
    }
}
