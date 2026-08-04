# ApotheosisGTNH

An Apotheosis-style affix loot system for GTNH (Minecraft 1.7.10). Mobs drop Tinkers' Construct parts/tools and vanilla gear with random affixes and pre-applied enchants. Pure Forge events -- zero runtime dependencies.

## Spawn Tiers

| Tier | Chance | Visual | Stat Buffs | Drops |
|---|---|---|---|---|
| **Boss** | 1-in-400 | Bold red name, always visible | +400% HP, +150% DMG, KB immune | Epic+ 100% |
| **Elite** | 1-in-50 | Gold name, always visible | +150% HP, +50% DMG | Rare+ 50% |
| **Carrier** | 1-in-500 | None (silent) | None | Uncommon 100% (player kill gated) |
| **Regular** | 1-in-50 (50% chance) | None | None | Full rarity table (weighted) |

All tiers tag mobs on `LivingSpawnEvent` (base event) -- catches natural spawns, spawners, and spawn eggs from any mod.

## 17 Affixes

### Weapon (Sword + Ranged -- 5)
| Affix | Levels | Effect |
|---|---|---|
| Sharp | 1-4 | +flat hearts damage |
| Vampiric | 1-10 | % of damage returned as healing |
| Knockback | 1-3 | Extra knockback |
| Fire | 1-5 | Set target on fire |
| Mending | 1-5 | Self-repair every N ticks |

### Tool (Pick/Axe/Shovel/Hoe -- 6)
| Affix | Levels | Effect |
|---|---|---|
| Haste | 1-3 | Haste potion effect |
| Auto-Smelt | 1 | Mined blocks drop smelted result |
| Telekinesis | 1 | Drops → player inventory |
| Fortune Boost | 1-3 | Bonus fortune levels |
| Unbreaking Boost | 1-3 | % chance to refund durability |
| Crop Growth | 1-2 | Right-click hoe → grow nearby crops |

### Armor (Chest/Legs/All -- 5)
| Affix | Levels | Effect |
|---|---|---|
| Armor | 1-? | Flat damage reduction |
| Guardian | 1-? | Flat DR on all armor slots |
| Thorns | 5-30 | % melee reflect |
| Heart | 1-5 | +max HP |
| Fortify | 1-3 | Resistance potion effect |

### Boots (3)
| Affix | Levels | Effect |
|---|---|---|
| Swift | 1-4 | Movement speed boost |
| Feather | 1-5 | Fall damage reduction |
| Gravity | 1-3 | Jump boost potion effect |

## Anvil Mechanics

All reforge/augment/upgrade operations run through the vanilla anvil. Which item you put in the right slot determines the operation.

### Reforging (Full Reroll)

| Right Slot | Left Slot | Result | Cost |
|---|---|---|---|
| Rarity Material (Common..Mythic) | Any affix-eligible item | New rarity + fresh affix set | XP levels + material count per rarity tier |

Wipes all existing affixes. Material rarity determines how many affixes roll (1-3). Works on un-affixed items too.

### Augmenting (Swap One Affix)

| Right Slot | Left Slot | Result | Cost |
|---|---|---|---|
| Augment Crystal (1-3 stack) | Affixed item | One affix replaced, rest unchanged | `stackSize` crystals + XP |

Stack count picks which affix to target: 1 = first, 2 = second, 3 = third. Affixes sorted alphabetically by ID. More crystals than affixes → rejected. Item rarity stays the same.

### Upgrading (Level Up One Affix)

| Right Slot | Left Slot | Result | Cost |
|---|---|---|---|
| Polishing Crystal (1-3 stack) | Affixed item | One affix +1 level, rest unchanged | `stackSize` crystals + XP |

Same stack-count targeting as augmenting. Rejects if the targeted affix is already at max level (e.g. Auto-Smelt is always capped at 1).

### Crafting

| Item | Recipe |
|---|---|
| Salvaging Hammer | 3 obsidian top row, 2 sticks center column (3 obsidian + 2 sticks) |
| Augment Crystal | Diamond center, 4 glowstone corners, 4 glass sides (1 diamond + 4 glowstone + 4 glass) |
| Polishing Crystal | Ender pearl center, diamond corners, nether brick sides (4 diamond + 4 nether brick + 1 ender pearl) |

### Salvaging (Crafting Table)

| Slots | Result |
|---|---|
| Salvaging Hammer + affixed item (any 2 slots) | Rarity material at the item's current rarity |

Hammer takes 1 durability damage per use (16 uses, container-item mechanic). Item is consumed. No XP cost.

### Costs

| Rarity | Affixes | Material Cost | XP Cost |
|---|---|---|---|
| Common | 1 | 1 | 3 |
| Uncommon | 2 | 2 | 6 |
| Rare | 2 | 2 | 15 |
| Epic | 3 | 3 | 25 |
| Mythic | 3 | 3 | 39 |

| Operation | Material Cost | XP Cost |
|---|---|---|
| Augment (swap) | Crystal count (1-3) | 5 |
| Upgrade (+1 level) | Crystal count (1-3) | 5 |
| Salvage | 1 hammer durability | 0 |

All configurable in `config/apogtnh.cfg`.

## Pre-Applied Enchants (Epic/Mythic)

| Slot | Enchants | Range |
|---|---|---|
| Sword | Sharpness + Unbreaking | Epic 5-15 / Mythic 15-25 |
| Bow | Power + Flame | Epic 5-15 / Mythic 15-25 |
| Pickaxe | Efficiency + Fortune + Unbreaking | Epic 5-15 / Mythic 15-25 |
| Armor | Protection + Unbreaking | Epic 5-15 / Mythic 15-25 |

## Tinkers' Construct Integration

- Tool head parts drop at common-rare tiers (Iron/Bronze → Steel/Alumite → Manyullyn/Cobalt/Thaumium)
- Fully assembled tools drop at epic/mythic with modifiers pre-applied
- Affix NBT carries through tool station assembly and part replacement
- Mending affix repairs Tinkers' InfiTool durability directly
- Mending will NOT resurrect a broken Tinkers tool (respects `InfiTool.Broken` flag)
- Armor and ranged weapons stay vanilla (Tinkers' has no armor in 1.7.10)

## Configuration

All values live in `config/apogtnh.cfg` (auto-generated on first run):

```properties
# Spawn chances (1-in-N, 0 disables)
bossSpawnChance=400
eliteSpawnChance=50
carrierSpawnChance=500

# Drop rates (percent)
bossDropPercent=100
eliteDropPercent=50
carrierDropPercent=100

# Regular mob drop
mobDropChance=50
mobDropPercent=50

# Affix cap
maxAffixesPerItem=3

# Master toggles
enableBossSpawns=true
enableEliteSpawns=true
enableCarrierDrops=true
enableMobDrops=true
enableReforging=true
enableAugmenting=true

# Per-rarity reforge costs
reforgeMaterialCostCommon=1
reforgeXpLevelCostCommon=3
# ... through Mythic

# Operation costs
augmentMaterialCost=1
augmentLevelCost=5
upgradeMaterialCost=1
upgradeLevelCost=5
salvageMaterialYield=1
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
