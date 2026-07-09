# ApotheosisGTNH

An Apotheosis-style affix loot system for GTNH (Minecraft 1.7.10). Mobs drop Tinkers' Construct parts and tools with random affixes and pre-applied XXV enchants. Zero runtime dependencies -- pure Forge events.

## Features

### Spawn Tiers

| Tier | Chance | Visual | Stat Buffs | Drops |
|---|---|---|---|---|
| **Boss** | 1-in-400 | Bold red name, always visible | +400% HP, +150% DMG, KB immune | Epic+ 100% |
| **Elite** | 1-in-50 | Gold name, always visible | +150% HP, +50% DMG | Rare+ 50% |
| **Carrier** | 1-in-500 | None (silent) | None | Uncommon 100% (player kill gated) |
| **Regular** | 1-in-50 (50% chance) | None | None | Full rarity table (weighted) |

All tiers work through `LivingSpawnEvent.SpecialSpawn` -- mobs from any mod (Angry Mobs, Special Mobs, etc.) get the boss/elite/carrier treatment.

### 14 Affixes

**Weapon affixes**
| Affix | Effect |
|---|---|
| Sharp | +flat damage per level |
| Vampiric | % of damage returned as healing (sword and ranged) |
| Forceful | Knockback strength |
| Scorching | Fire duration |
| Mending | Self-repair every N ticks (vanilla and Tinkers' durability) |

**Armor affixes**
| Affix | Slot | Effect |
|---|---|---|
| Sturdy | All armor | Flat damage reduction |
| Guardian | All armor | Flat damage reduction per piece |
| Barbed | Chestplate | % melee reflect |
| Vital | Chest/Legs | +max HP |
| Fortify | Chestplate | Resistance potion effect |

**Boot affixes**
| Affix | Effect |
|---|---|
| Swift | Movement speed boost |
| Feather | Fall damage reduction |
| Spring | Jump boost potion effect |

**Tool affixes**
| Affix | Effect |
|---|---|
| Haste | Haste potion effect |

### Tinkers' Construct Integration

- Tool head parts drop at common-rare tiers (material tiered by rarity -- Iron/Bronze → Steel/Alumite → Manyullyn/Cobalt/Thaumium)
- Fully assembled tools drop at epic/mythic with modifiers pre-applied (quartz for damage, lapis for fortune, redstone for haste)
- Affix NBT carries through tool station assembly and part replacement
- Mending affix repairs Tinkers' InfiTool durability directly
- Armor and ranged weapons stay vanilla (Tinkers' has no armor in 1.7.10)

### Pre-Applied Enchants (Epic/Mythic)

| Slot | Enchants | Range |
|---|---|---|
| Sword | Sharpness + Unbreaking | Epic 5-15 / Mythic 15-25 |
| Bow | Power + Flame | Epic 5-15 / Mythic 15-25 |
| Pickaxe | Efficiency + Fortune + Unbreaking | Epic 5-15 / Mythic 15-25 |
| Armor | Protection + Unbreaking | Epic 5-15 / Mythic 15-25 |

Enchant levels render as Roman numerals matching vanilla style.

## Configuration

All values live in `config/apogtnh.cfg` (auto-generated on first run):

```properties
# Spawn chances (1-in-N, 0 disables)
bossSpawnChance=400
eliteSpawnChance=50
carrierSpawnChance=500

# Regular mob drop (1-in-N, 0 disables)
mobDropChance=50
mobDropPercent=50

# Drop rates by tier (percent)
bossDropPercent=100
eliteDropPercent=50
carrierDropPercent=100

# Affix cap
maxAffixesPerItem=3

# Master toggles
enableBossSpawns=true
enableEliteSpawns=true
enableCarrierDrops=true
enableMobDrops=true
```

## Build

Requires Java 17-25 and a GTNH development workspace.

```
./gradlew setupDecompWorkspace
./gradlew build
```

Output jars in `build/libs/` -- dev, sources, and obfuscated.

## License

MIT
