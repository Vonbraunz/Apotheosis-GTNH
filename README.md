# ApotheosisGTNH — Personal 1.7.10 Backport

A stripped-down Apotheosis-style affix loot and reforging system for GTNH.

## Scope (locked in)

**In:**
- Affix loot system (weapons/tools/armor) with rarity tiers
- Boss / Elite / Carrier drop tiers via `LivingDropsEvent` + `LivingSpawnEvent` + `PopulateChunkEvent`
- Simplified reforge table (one sigil type, XP cost, re-roll all affixes)
- Small subset of straight-extends enchantments (optional, examples included)

**Out:**
- Chest loot injection (`ChestGenHooks` untouched)
- Full Placebo library (only what Deadly needs is stubbed)
- Garden module, Prismatic Altar, spawner module, potion module
- CraftTweaker / WAILA / NEI compat (optional add-back later)
- Enchantment scraps, typed books, anvil ASM

## Stack

- Target: Minecraft 1.7.10, Forge 10.13.4.1614+
- Runtime: Java 17-25 via GTNH Lwjgl3ify (bytecode: Java 8)
- Coremod: **Mixins** via UniMixins (no `IFMLLoadingPlugin`)
- Mixin Extras: bundled with UniMixins in GTNH, safe to depend on

## Build

Uses the GTNH gradle plugin. From project root:

```
./gradlew setupDecompWorkspace
./gradlew runClient
./gradlew build
```

Output jar in `build/libs/`.

## Layout

```
src/main/java/com/vonbraunz/apogtnh/
├── ApotheosisGTNH.java         entry point, @Mod
├── ApoConfig.java              config
├── proxy/                      SidedProxy
├── placebo/                    minimum Placebo API surface
├── affix/                      affix framework + example impls
├── deadly/                     boss/elite/carrier tagging + events
├── reforge/                    reforging table block/TE/container/GUI
├── network/                    SimpleNetworkWrapper packets
└── mixin/                      mixin classes (referenced from JSON)
src/main/resources/
├── mcmod.info
├── mixins.apotheosis.json      mixin config
└── assets/apogtnh/             textures/lang
```

## TODOs left for implementation

Every file has `TODO(scaffold)` markers where real logic goes. Enough structure to compile once you fill the TODOs; enough shape to know where each piece belongs.

## Rename

If you don't want `com.vonbraunz.apogtnh` as the package, batch-rename before you start filling TODOs. The mod ID string `apogtnh` also appears in `mcmod.info`, `mixins.apotheosis.json`, `ApotheosisGTNH.java`, and the resource asset paths.
