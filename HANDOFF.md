# Handoff — What's here and what's left

Scaffold state: ~25 files, structural only. Every TODO(scaffold) is a real gap.

## Files present

```
build.gradle, settings.gradle, gradle.properties, dependencies.gradle
mcmod.info, mixins.apotheosis.json
en_US.lang, TEXTURES_TODO.txt

ApotheosisGTNH.java        entry point, @Mod, event bus registration
ApoConfig.java             all tunable numbers + master toggles

proxy/CommonProxy.java
proxy/ClientProxy.java

placebo/PlaceboUtil.java   NBT helpers
placebo/AttributeHelper.java   modifier apply/remove
placebo/RegistryList.java

affix/Affix.java              base class
affix/LootRarity.java         enum + weighted roll
affix/LootCategory.java       enum + item classification
affix/AffixRegistry.java      registry + bootstrap
affix/AffixHelper.java        NBT layout, rolling, tooltip
affix/impl/AffixDamage.java   +flat damage
affix/impl/AffixLifesteal.java   heal on hit
affix/impl/AffixArmor.java    flat damage reduction

deadly/DeadlyTags.java        entity NBT tag scheme
deadly/EliteMarker.java       stat buffs + visual tell
deadly/DeadlyEventHandler.java   spawn tag, drops, damage hooks
deadly/BossPlacer.java        PopulateChunkEvent boss spawn

reforge/ReforgeContent.java   registration
reforge/BlockReforgingTable.java
reforge/TileReforgingTable.java
reforge/ContainerReforging.java   doReforge action
reforge/GuiReforging.java
reforge/ReforgeGuiHandler.java
reforge/ItemSigil.java

network/ReforgePacket.java    button click → server action

mixin/MixinEnchantmentHelper.java   example only, safe to delete
```

## Implementation order

Recommended sequence so you have something working at each stage:

1. **Get it compiling.**
   - Drop in the current ExampleMod1.7.10 build.gradle / settings.gradle / gradle.properties files;
     keep only the ApoGTNH-specific overrides.
   - `./gradlew setupDecompWorkspace` and confirm workspace resolves.
   - `./gradlew build` — expect compile errors on mapping mismatches (the
     `func_150296_c` note in AffixHelper, the `enablePersistence` note in BossPlacer, etc.).
     Fix those in place using your workspace's resolved names.

2. **Wire the mod entry point.**
   - ApotheosisGTNH#init: register `ReforgeGuiHandler` with `NetworkRegistry`, register
     `ReforgePacket` with a `SimpleNetworkWrapper`, save the channel to a static.
   - GuiReforging#actionPerformed: `channel.sendToServer(new ReforgePacket())`.

3. **First smoke test — affix drops only, no reforge, no bosses.**
   - Disable enableBossSpawns and enableReforgeTable in config.
   - `/summon Zombie` isn't a vanilla 1.7.10 command but a test mod / creative kill of a
     naturally-spawned mob works. Or add a debug command in serverStarting.
   - You should see: random hostile mob spawns → occasionally becomes elite (visible name
     tag) → dies → drops a diamond sword with an affix rolled in the tooltip.

4. **Add reforge table.**
   - Re-enable config toggle. Craft/give a Reforging Table and a Sigil.
   - Right-click table → GUI opens → put affix sword + sigil in → click Reforge →
     item name/stats reroll, sigil consumed, XP taken.
   - Recipe for both (crafting table shaped) is TODO — add to ReforgeContent.register().

5. **Add boss spawns.**
   - Enable enableBossSpawns. Fly around new chunks, verify boss zombies appear.
   - Tune bossChunkChance until it feels right.

6. **Expand affix pool.**
   - AffixRegistry.bootstrap() is where new affixes register. Follow the pattern of
     AffixDamage/AffixArmor. Realistic goal: 15-25 affixes total for good variety.

7. **Expand base item pool.**
   - DeadlyEventHandler#rollAffixItem currently returns a hardcoded diamond sword.
     Replace with a weighted pool driven by rarity — e.g. common rolls iron/gold gear,
     mythic rolls diamond/GT-tier gear.

## Known gaps (things I stubbed but didn't finish)

- **Container transferStackInSlot** (shift-click) is `return null`. Vanilla behavior
  works but shift-click won't move items into the tile. Standard boilerplate;
  copy from any vanilla container's implementation.
- **Recipe registration** — no crafting recipes exist for the table or sigils.
  Add `GameRegistry.addRecipe(new ItemStack(reforgingTable), ...)` in ReforgeContent.
- **Textures** — none provided. See TEXTURES_TODO.txt. Missing textures render
  as the vanilla checkerboard, which is fine for early smoke testing.
- **Boss variety** — BossPlacer spawns only a buffed zombie. Add skeleton/creeper/spider
  variants weighted by biome.
- **Elite despawn prevention** — commented but not called. Bosses should probably
  use `boss.func_110163_bv()` (enablePersistence). Verify MCP name in your workspace.
- **Affix tooltip injection** — AffixHelper#buildTooltipLines exists but nothing
  calls it. Subscribe to `ItemTooltipEvent` in DeadlyEventHandler and append the lines.
- **Client sync of elite name tag** — setCustomNameTag is server-side; vanilla syncs
  it automatically for entities, but if you notice elites showing plain names on
  client, check DataWatcher setup or use a proper packet.
- **The mixin does nothing useful** — MixinEnchantmentHelper is a pattern example.
  If you decide LivingHurtEvent covers all your damage math, delete the mixin
  class and the entry in mixins.apotheosis.json. Zero cost.

## Compilation risks specific to GTNH workspace

- **NBTTagCompound#getKeySet** — in AffixHelper, called as `func_150296_c()`. If
  GTNH's mapping provides `getKeySet`, swap it.
- **enablePersistence** — in BossPlacer, commented out. In vanilla 1.7.10 MCP it's
  `func_110163_bv()`.
- **Attribute IAttributeInstance.getModifier / applyModifier / removeModifier** — used
  in AttributeHelper. Signatures should match 1.7.10 exactly.
- **LivingHurtEvent.ammount** — spelling. This is the correct Forge 1.7.10 field name;
  do not "fix" it or the compile breaks.

## Rough timeline (if you actually build this)

- Steps 1-2: half day
- Step 3 (first drops working): half day
- Step 4 (reforge working): weekend
- Step 5 (boss spawns tuned): half day
- Step 6 (affix pool expansion): weekend, ongoing
- Step 7 (base item pool): half day

Total to functional MVP: ~2 weekends of focused work.
Total to polished personal build: 4-6 weekends.
