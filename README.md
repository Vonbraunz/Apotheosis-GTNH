# ApotheosisGTNH

An Apotheosis-style affix loot system for GTNH (Minecraft 1.7.10). Mobs drop gear with random affixes; elites, carriers, and bosses guarantee higher-tier drops with pre-applied XXV enchants. Fully integrated with Tinkers' Construct.

## Features

- **10 affixes** across weapons and armor (Sharp, Vampiric, Forceful, Scorching, Sturdy, Barbed, Guardian, Vital, Swift, Feather)
- **4 drop tiers**: regular mobs (1-in-200), silent carriers (1-in-500), elite mobs with visible name tags (1-in-50), boss mobs with massive buffs (1-in-400)
- **Epic/Mythic gear** pre-enchanted with XXV enchants (Sharpness, Protection, Efficiency, Fortune, Power, Flame, Unbreaking)
- **Tinkers' Construct integration**: tool head parts drop at common-rare tiers, fully assembled tools with modifiers at epic-mythic. Affix NBT carries through tool station assembly.
- **Zero runtime dependencies**: no mixins, no packets, no library mods required. Pure Forge events.

## Build

Requires Java 17-25 and a GTNH workspace.

```
./gradlew setupDecompWorkspace
./gradlew build
```

Output jar in `build/libs/`.

## Configuration

All values live in `config/apogtnh.cfg` (auto-generated on first run):

```properties
# Spawn chances (1-in-N, 0 disables)
bossSpawnChance=400
eliteSpawnChance=50
carrierSpawnChance=500

# Regular mob drop (1-in-N, 0 disables)
mobDropChance=200
mobDropPercent=30

# Drop rates by tier (percent)
bossDropPercent=100
eliteDropPercent=50
carrierDropPercent=100

# Affix cap
maxAffixesPerItem=3
```

## License

MIT
