# ApotheosisGTNH

An Apotheosis-style affix loot system for GTNH (Minecraft 1.7.10). Mobs drop Tinkers' Construct parts/tools and vanilla gear with random affixes and pre-applied enchants. Pure Forge events + UniMixins for reach/bow mechanics. Zero runtime dependencies beyond what ships with GTNH.

## Quick Reference

| Mechanic | Where | How |
|---|---|---|
| **Reforge** (reroll all) | Anvil | Rarity Material in right slot |
| **Augment** (swap one) | Anvil | Augment Crystal, stack count = affix slot |
| **Upgrade** (+1 level) | Anvil | Polishing Crystal, stack count = affix slot |
| **Salvage** (item → material) | Crafting Table | Hammer + affix item in any 2 slots |
| **Apply Book** (creative) | Anvil | Affix Book in right slot |

## 35 Affixes

Hold **Shift** while hovering an affix item for detailed descriptions.

### Weapon (Sword -- 10)
| Affix | Levels | Effect |
|---|---|---|
| Sharp | 1-4 | +flat hearts damage |
| Vampiric | 1-10 | % damage returned as healing |
| Knockback | 1-3 | Extra knockback |
| Fire | 1-5 | Set target on fire |
| Mending | 1-5 | Self-repair every N ticks |
| Beheading | 1-3 | 5/10/15% skull drop chance |
| Execute | 1-3 | Instakill below 5/10/15% HP |
| Lacerating | 1-3 | 10/20/30% chance double damage |
| Frost | 1-3 | Slowness on hit |

### Weapon + Tool (Sword + Axe -- 2)
| Affix | Levels | Effect |
|---|---|---|
| Cleaving | 1-3 | AOE: hit 3/4/5 targets, radius 2/3/4 blocks |
| Shredding | 1-3 | 5/10/15% max HP as bonus damage |

### Ranged (Bow / Crossbow -- 7)
| Affix | Levels | Effect |
|---|---|---|
| Velocity | 1-3 | +25/50/75% arrow speed |
| Multishot | 1 | Fires 3 arrows per shot |
| Draw Speed | 1-3 | Faster charge (13/10/8 ticks) |
| Surgical | 1-3 | +25/50/75% crit on arrow hit |
| Explosive | 1 | TNT blast on arrow impact |
| Piercing | 1-3 | +2/4/6 flat damage per arrow |
| Venom | 1-3 | Poison on hit |

### Tool (Pick/Axe/Shovel/Hoe -- 7)
| Affix | Levels | Effect |
|---|---|---|
| Haste | 1-3 | Haste potion effect |
| Auto-Smelt | 1 | Mined blocks → smelted result |
| Telekinesis | 1 | Drops → player inventory |
| Fortune Boost | 1-3 | Bonus fortune levels |
| Unbreaking Boost | 1-3 | % chance durability refund |
| Crop Growth | 1-2 | Right-click hoe → 3x3/5x5 growth |
| Reach | 1 | +1 block interaction range |

### Helmet (1)
| Affix | Levels | Effect |
|---|---|---|
| Aquatic | 1 | Permanent Water Breathing |

### Chestplate (3)
| Affix | Levels | Effect |
|---|---|---|
| Fortify | 1-3 | Resistance potion effect |
| Revitalizing | 1-3 | Regeneration potion effect |
| Thorns | 5-30 | % melee reflect |

### Chest + Legs (1)
| Affix | Levels | Effect |
|---|---|---|
| Heart | 1-5 | +max HP (2 hearts per level) |

### All Armor (2)
| Affix | Levels | Effect |
|---|---|---|
| Armor | 1-? | Flat damage reduction |
| Guardian | 1-? | Flat DR on all armor slots |

### Boots (5)
| Affix | Levels | Effect |
|---|---|---|
| Swift | 1-4 | Movement speed boost |
| Feather | 1-5 | Fall damage reduction |
| Gravity | 1-3 | Jump boost potion |
| Step Assist | 1-3 | Auto-step 1/2/3 blocks |
| Flame-Walker | 1 | Permanent Fire Resistance |

### Level Caps by Rarity
| Rarity | Max Affix Level |
|---|---|
| Common | 1 |
| Uncommon | 2 |
| Rare | 3 |
| Epic | 4 |
| Mythic | Uncapped |

Polishing crystals can push affixes beyond their rarity cap -- the cap only applies to initial rolls and augment swaps.

## Rarity Tiers

| Rarity | Affix Count | Color | Drop Weight |
|---|---|---|---|
| Common | 1 | White | 200 |
| Uncommon | 2 | Yellow | 100 |
| Rare | 2 | Aqua | 40 |
| Epic | 3 | Light Purple | 10 |
| Mythic | 4 | Gold | 2 |

## Spawn Tiers

| Tier | Chance | Visual | Stat Buffs | Drops |
|---|---|---|---|---|
| **Boss** | 1-in-400 | Bold red name | +400% HP, +150% DMG, KB immune | Epic+ 100% |
| **Elite** | 1-in-50 | Gold name | +150% HP, +50% DMG | Rare+ 50% |
| **Carrier** | 1-in-500 | None (silent) | None | Uncommon 100% (player kill gated) |
| **Regular** | 1-in-50 (50% chance) | None | None | Full rarity table (weighted) |

All tiers tag mobs on `LivingSpawnEvent` (base event) -- catches natural spawns, spawners, and spawn eggs from any mod.

## Anvil Mechanics

### Reforging (Full Reroll)
**Right slot:** Rarity Material (Common..Mythic)
**Left slot:** Any affix-eligible item
**Result:** New rarity + fresh affix set
**Cost:** XP + material count per rarity tier

### Augmenting (Swap One Affix)
**Right slot:** Augment Crystal (stack 1-4)
**Left slot:** Affixed item
**Result:** One affix replaced, rest unchanged
**Cost:** `stackSize` crystals + 5 XP
Stack count targets affix by alphabetical ID order. More crystals than affixes = rejected.

### Upgrading (Level Up One Affix)
**Right slot:** Polishing Crystal (stack 1-4)
**Left slot:** Affixed item
**Result:** One affix +1 level
**Cost:** `stackSize` crystals + 5 XP
Rejects if targeted affix is already at max level.

### Applying Books (Creative Mode)
**Right slot:** Affix Book (subtype 0-34)
**Left slot:** Any affix-eligible item
**Result:** Book's affix applied at level 1
**Cost:** 1 book + 1 XP

### Costs
| Rarity | Material Cost | XP Cost |
|---|---|---|
| Common | 1 | 3 |
| Uncommon | 2 | 6 |
| Rare | 2 | 15 |
| Epic | 3 | 25 |
| Mythic | 3 | 39 |

| Operation | Cost |
|---|---|
| Augment (swap) | Crystal count + 5 XP |
| Upgrade (+1 level) | Crystal count + 5 XP |
| Salvage | 1 hammer durability + 0 XP |
| Apply Book | 1 book + 1 XP |

## Crafting

| Item | Recipe |
|---|---|
| Salvaging Hammer | 3 obsidian top row, 2 sticks center column |
| Augment Crystal | Diamond center, 4 glowstone corners, 4 glass sides |
| Polishing Crystal | Ender pearl center, diamond corners, nether brick sides |

### Salvaging (Crafting Table)
Hammer + affixed item in any 2 slots → rarity material at the item's current rarity. Hammer takes 1 durability per use (16 uses, container-item mechanic). Item is consumed.

## Tinkers' Construct Integration

- Tool head parts drop at common-rare tiers (Iron/Bronze → Steel/Alumite → Manyullyn/Cobalt/Thaumium)
- Fully assembled tools drop at epic/mythic with modifiers pre-applied
- Affix NBT carries through tool station assembly and part replacement
- Mending affix repairs Tinkers' InfiTool durability directly
- Mending will NOT resurrect a broken Tinkers tool (respects `InfiTool.Broken` flag)
- Crossbows classified as RANGED, bolts/arrows as RANGED (not TOOL)
- Bow/arrow affixes work with Tinkers bows, crossbows, and ammo via mixin

## Mixins (UniMixins)

| Mixin | Target | Purpose |
|---|---|---|
| MixinPlayerControllerMP | PlayerControllerMP | Reach affix: extends `getBlockReachDistance()` |
| MixinItemBow | ItemBow | Draw Speed, Velocity, Multishot; stashes bow affix NBT for arrow entity |
| MixinEntityArrow | EntityArrow | Reads stashed bow NBT on arrow spawn for on-impact arrow affixes |

All mixins use `@SideOnly(CLIENT)` or guard with `world.isRemote` where appropriate. No server-side class references in client mixins.

## Configuration

All values in `config/apogtnh.cfg` (auto-generated on first run):

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
maxAffixesPerItem=4

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
