# Affix Reroll + Tool Affixes — Handoff

Branch: `AffixReroll`. Three mechanics — reforging, augmenting, salvaging — all run through
one vanilla anvil handler. No custom blocks, tiles, containers, or GUIs anywhere in this
branch. Every `TODO` still in the code is a real gap.

## Compile status

Confirmed clean through the reforge/augment anvil merge (previous version of this doc) —
GTNH mappings v12 resolved every `AnvilUpdateEvent`/`IInventory`/`HarvestDropsEvent` field
and method name used. Tool affixes (`ToolAffixEventHandler` + all 5 `affix/impl/Affix*`
classes) were implemented and confirmed working in-game since then, including the
`IGrowable.func_149851_a/func_149852_a/func_149853_b` crop-growth calls and the
`PlayerEvent.HarvestCheck` durability-refund timing.

**The handler rename + salvage branch (this pass) has not been re-verified** — same sandbox
limitation as always, `gradlew` can't establish a loopback connection here. Run it
yourself:

```bash
./gradlew.bat compileJava
```

`grep -r AnvilRerollHandler src/` comes back empty (old name fully replaced by
`AnvilHandler`), so there shouldn't be dangling references, but that's not a substitute for
an actual compile.

## Architecture (current)

One block-free economy loop, all three mechanics living in `AnvilHandler`
(`net.minecraftforge.event.AnvilUpdateEvent`), distinguished entirely by the right-slot
item:

| Right slot         | Mechanic  | Effect                                                         | Status |
|---------------------|-----------|------------------------------------------------------------------|--------|
| `ItemRarityMaterial` | Reforge   | Wipes affixes, rolls a fresh set at the material's rarity tier  | stubbed (`ReforgeController`) |
| `ItemAugmentCrystal` | Augment   | Rerolls 1 random affix, keeps the rest + current rarity          | stubbed (`ReforgeController`) |
| `ItemSalvageSigil`   | Salvage   | Destroys an affixed item, returns 1 rarity material at its rarity | **fully implemented** |

Salvage closes the loop that reforging needed: `ItemRarityMaterial` had no obtain path
before this (`/give`-only). Now: craft a Salvage Sigil (vanilla ingredients) → salvage
affixed items you don't want → get rarity materials → spend those to reforge items you do
want.

One handler class, not three independent subscribers — Forge doesn't guarantee ordering
between multiple listeners setting `event.output`, so all three branches live in one
`onAnvilUpdate` method that dispatches by right-slot item type instead.

**Vanilla anvil gotcha**: `ContainerRepair` discards `event.output` if `event.cost <= 0`.
Salvage isn't conceptually an XP sink, but `handleSalvage` still sets
`event.cost = ApoConfig.salvageLevelCost` (default 1, config min is enforced at 1) purely
to satisfy that vanilla check — don't "simplify" this to 0, it'll silently break salvaging.

## Files present

```
ApotheosisGTNH.java          @Instance field; preInit calls ModContent.preInit(); init
                              registers DeadlyEventHandler, ToolAffixEventHandler,
                              AnvilHandler (renamed from AnvilRerollHandler).
ApoConfig.java                "reforge" config category: enableReforging/enableAugmenting/
                              enableSalvaging toggles, per-rarity reforge material/XP costs,
                              flat augmentMaterialCost/augmentLevelCost, flat
                              salvageSigilCost/salvageLevelCost/salvageMaterialYield.
ModContent.java                registers rarityMaterial, augmentCrystal, salvageSigil
                              (items only); registerRecipes() adds the Salvage Sigil's
                              shaped crafting recipe.

affix/Affix.java               +onHarvestDrops, +onHoeUse hooks
affix/AffixRegistry.java       registers the 5 tool affixes
affix/ToolAffixEventHandler.java   fully implemented -- HarvestCheck (durability snapshot),
                              HarvestDrops (Fortune Boost -> Auto-Smelt -> Telekinesis
                              ordering, then Unbreaking Boost refund check), PlayerInteract
                              (hoe crop growth)
affix/impl/AffixAutoSmelt.java        implemented (FurnaceRecipes lookup per drop)
affix/impl/AffixTelekinesis.java      implemented (inventory insert, world-spawn fallback)
affix/impl/AffixFortuneBoost.java     implemented (block.getDrops with boosted fortune)
affix/impl/AffixUnbreakingBoost.java  implemented (chancePercent = 100/(level+1))
affix/impl/AffixCropGrowth.java       implemented (IGrowable radius scan by level)

reforge/ReforgeController.java   shared reforge/augment logic -- STILL STUBBED:
                              rollPreview returns an unmodified copy, getAlternativeAffixes
                              returns null, upgrade is a no-op. This is the one remaining
                              piece of real reroll math. Salvage doesn't use this class at
                              all (it's a straight rarity lookup, no controller needed).
reforge/ReforgeCost.java         ApoConfig accessor for reforge costs, fully implemented.
reforge/AnvilHandler.java        THE handler for all three mechanics (renamed from
                              AnvilRerollHandler now that it's not just reroll anymore).
reforge/item/ItemRarityMaterial.java   5-subtype meta-item, fully wired. Drives reforging.
                              No crafting recipe -- /give or salvage only.
reforge/item/ItemAugmentCrystal.java   single-variant item. Has a texture now
                              (textures/items/augment_crystal.png, copied from modern
                              Apotheosis's sigils/enhancement.png). No crafting recipe yet.
reforge/item/ItemSalvageSigil.java     NEW: single-variant item. Texture copied from modern
                              Apotheosis's sigils/rebirth.png. Has a crafting recipe (see
                              below). Drives salvaging.
```

Removed in earlier passes (all superseded by the anvil pivot, see git history if you need
them): `BlockReforgingTable`, `BlockAugmentingTable`, every `TileEntity*`/`Container*`/
`Gui*` for those blocks, `ReforgeGuiHandler`, and `AnvilRerollHandler` (renamed to
`AnvilHandler`, not deleted-and-recreated — same logic, new name, plus the salvage branch).

## Assets

```
textures/items/rarity_material_*.png     used by ItemRarityMaterial (5 subtypes)
textures/items/augment_crystal.png       used by ItemAugmentCrystal
textures/items/salvage_sigil.png         used by ItemSalvageSigil
```
No block textures anywhere -- no blocks exist in this branch.

## Salvage Sigil recipe

```
S S S
G G G     S = Items.glowstone_dust
S S S     G = Blocks.obsidian
→ 6x ItemSalvageSigil
```

Modern Apotheosis's actual Sigil of Rebirth recipe (3x3, gem_fused_slate border /
gem_dust center) doesn't port: `gem_fused_slate` requires `minecraft:deepslate` (1.17+,
doesn't exist in 1.7.10), and `gem_dust` has no crafting recipe upstream at all (loot-only).
This substitutes vanilla ingredients while keeping the same border+center shape and yield
of 6. Registered in `ModContent.registerRecipes()` via `GameRegistry.addRecipe`.

## Known issues remaining

- **`ReforgeController` is still fully stubbed** — reforge and augment both no-op until
  `rollPreview`/`getAlternativeAffixes`/`upgrade` are implemented. This is the single
  highest-value remaining task; salvage doesn't depend on it and already works once
  compiled.
- **`ItemRarityMaterial` and `ItemAugmentCrystal` have no crafting recipes** — only
  `/give`, or (for rarity material) salvaging, which itself requires a Salvage Sigil, which
  itself requires glowstone dust + obsidian. That's now a complete, if slightly long,
  obtain chain. Augment Crystal is still a dead end — needs its own vanilla recipe.
- **No level-cost cap check** — vanilla anvils refuse to open above 39 XP levels unless
  creative. `reforgeXpLevelCostMythic` is already set to 39 (was fixed from 40 in an
  earlier pass). `salvageLevelCost` defaults to 1, well under the cap.

## Implementation order

1. **`ReforgeController`** — do this first, it unblocks reforge and augment simultaneously.
   Salvage already works without it.
2. **Smoke test all three anvil branches** in-game once `ReforgeController` lands.
3. **Augment Crystal recipe** — pick vanilla ingredients (or another salvage-adjacent
   source) so it's not `/give`-only like rarity material used to be.
4. **Polish** — anything else in `ApoConfig`'s `reforge` category worth exposing, e.g. a
   per-rarity salvage yield instead of the current flat `salvageMaterialYield`, if testing
   shows the flat rate feels off at higher rarities.

## Rough timeline

- Step 1 (`ReforgeController`): a few hours, pure NBT/list manipulation.
- Step 2 (smoke test): trivial once step 1 lands.
- Step 3 (Augment Crystal recipe): minutes, same pattern as the Salvage Sigil recipe above.
- Step 4 (polish): ongoing, not blocking functionality.

Total to a fully functional MVP: the only real remaining work is `ReforgeController` --
everything else (registration, economy loop, tool affixes) is done and just needs a
compile to confirm.
