package com.vonbraunz.apogtnh.affix;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

import com.vonbraunz.apogtnh.affix.impl.AffixUnbreakingBoost;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Forge event subscriber for the TOOL-category affixes (Auto-Smelt, Telekinesis,
 * Fortune Boost, Unbreaking Boost, Crop Growth).
 *
 * Ordering in onHarvestDrops matters: Fortune Boost runs first so the boosted drop
 * count feeds into Auto-Smelt, which then feeds into Telekinesis.
 */
public class ToolAffixEventHandler {

    // snapshot of held-item durability before a harvest block-break, keyed by player.
    // consumed by the Unbreaking Boost compensate-after check in onHarvestDrops.
    private final Map<EntityPlayer, Integer> pendingDamage = new HashMap<EntityPlayer, Integer>();

    @SubscribeEvent
    public void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        if (event.entityPlayer == null) return;
        ItemStack held = event.entityPlayer.getHeldItem();
        if (held == null || !held.isItemStackDamageable() || !AffixHelper.hasAffixData(held)) return;
        pendingDamage.put(event.entityPlayer, held.getItemDamage());
    }

    @SubscribeEvent
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        ItemStack tool = event.harvester == null ? null : event.harvester.getHeldItem();
        if (tool == null || !AffixHelper.hasAffixData(tool)) return;

        int vanillaFortune = event.fortuneLevel;

        // phase 1: affixes that modify the drop list -- run fortune boost first so
        // auto-smelt sees the boosted quantity
        for (Map.Entry<Affix, Integer> e : AffixHelper.getAffixes(tool)
            .entrySet()) {
            Affix affix = e.getKey();
            int level = e.getValue();
            if (!affix.categories.contains(LootCategory.TOOL)) continue;

            // Fortune Boost runs before Auto-Smelt by convention -- we check both here
            // but order them by calling Fortune Boost explicitly first
            if (affix.id.equals("apogtnh:fortune_boost")) {
                affix.onHarvestDrops(
                    tool,
                    level,
                    event.harvester,
                    event.world,
                    event.x,
                    event.y,
                    event.z,
                    event.block,
                    event.blockMetadata,
                    event.drops,
                    vanillaFortune);
            }
        }
        for (Map.Entry<Affix, Integer> e : AffixHelper.getAffixes(tool)
            .entrySet()) {
            Affix affix = e.getKey();
            int level = e.getValue();
            if (!affix.categories.contains(LootCategory.TOOL)) continue;

            if (affix.id.equals("apogtnh:auto_smelt")) {
                affix.onHarvestDrops(
                    tool,
                    level,
                    event.harvester,
                    event.world,
                    event.x,
                    event.y,
                    event.z,
                    event.block,
                    event.blockMetadata,
                    event.drops,
                    vanillaFortune);
            }
        }

        // phase 2: telekinesis is last in the chain -- it moves whatever's left
        for (Map.Entry<Affix, Integer> e : AffixHelper.getAffixes(tool)
            .entrySet()) {
            Affix affix = e.getKey();
            int level = e.getValue();
            if (!affix.categories.contains(LootCategory.TOOL)) continue;

            if (affix.id.equals("apogtnh:telekinesis")) {
                affix.onHarvestDrops(
                    tool,
                    level,
                    event.harvester,
                    event.world,
                    event.x,
                    event.y,
                    event.z,
                    event.block,
                    event.blockMetadata,
                    event.drops,
                    vanillaFortune);
            }
        }

        // phase 3: unbreaking boost refund -- runs after the drop chain is done
        Integer beforeDamage = pendingDamage.remove(event.harvester);
        if (beforeDamage != null) {
            for (Map.Entry<Affix, Integer> e : AffixHelper.getAffixes(tool)
                .entrySet()) {
                if (e.getKey() instanceof AffixUnbreakingBoost) {
                    int chance = ((AffixUnbreakingBoost) e.getKey()).chancePercent(e.getValue());
                    int afterDamage = tool.getItemDamage();
                    if (afterDamage > beforeDamage && event.world.rand.nextInt(100) < chance) {
                        tool.setItemDamage(beforeDamage);
                    }
                    break; // only one unbreaking boost affix can be present
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!(event instanceof PlayerInteractEvent)) return;

        ItemStack held = event.entityPlayer == null ? null : event.entityPlayer.getHeldItem();
        if (held == null || !(held.getItem() instanceof ItemHoe) || !AffixHelper.hasAffixData(held)) return;

        int x = event.x;
        int y = event.y;
        int z = event.z;
        if (x < 0) return; // right-clicking air gives negative coordinates

        for (Map.Entry<Affix, Integer> e : AffixHelper.getAffixes(held)
            .entrySet()) {
            if (e.getKey()
                .onHoeUse(held, e.getValue(), event.entityPlayer, event.entityPlayer.worldObj, x, y, z)) {
                event.setCanceled(true);
            }
        }
    }
}
