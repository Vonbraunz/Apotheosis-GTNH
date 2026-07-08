package com.vonbraunz.apogtnh.deadly;

import java.util.Map;
import java.util.Random;

import com.vonbraunz.apogtnh.ApoConfig;
import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.AffixHelper;
import com.vonbraunz.apogtnh.affix.LootRarity;
import com.vonbraunz.apogtnh.deadly.DeadlyTags.Tier;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

/**
 * Central Forge event bus subscriber.
 *
 * Responsibilities:
 *   1. Tag mobs at spawn (elite / carrier)
 *   2. Convert tags into affix drops at death (tagged mobs + regular mobs)
 *   3. Fire affix hooks on damage dealt / taken / tick
 *   4. Inject affix data into item tooltips
 */
public class DeadlyEventHandler {

    // ==== Spawn tagging =====================================================

    @SubscribeEvent
    public void onSpawn(LivingSpawnEvent.SpecialSpawn event) {
        EntityLivingBase entity = event.entityLiving;
        if (!(entity instanceof IMob)) return;
        if (DeadlyTags.isTagged(entity)) return;

        Random rand = entity.worldObj.rand;

        if (ApoConfig.enableEliteSpawns && ApoConfig.eliteSpawnChance > 0
            && rand.nextInt(ApoConfig.eliteSpawnChance) == 0) {
            DeadlyTags.tag(entity, Tier.ELITE);
            EliteMarker.applyElite(entity);
            return;
        }

        if (ApoConfig.enableCarrierDrops && ApoConfig.carrierSpawnChance > 0
            && rand.nextInt(ApoConfig.carrierSpawnChance) == 0) {
            DeadlyTags.tag(entity, Tier.CARRIER);
        }
    }

    // ==== Drops =============================================================

    @SubscribeEvent
    public void onDrops(LivingDropsEvent event) {
        EntityLivingBase entity = event.entityLiving;
        if (!(entity instanceof IMob)) return;

        Random rand = entity.worldObj.rand;
        Tier tier = DeadlyTags.getTier(entity);

        if (tier == null) {
            // regular untagged mob -- small chance at low-rarity affix drop
            if (!ApoConfig.enableMobDrops || ApoConfig.mobDropChance <= 0) return;
            if (rand.nextInt(ApoConfig.mobDropChance) != 0) return;
            if (rand.nextInt(100) >= ApoConfig.mobDropPercent) return;

            LootRarity rarity = LootRarity.rollCapped(rand, LootRarity.UNCOMMON);
            ItemStack loot = rollAffixItem(rand, rarity);
            if (loot != null) spawnDrop(entity, loot);
            return;
        }

        // tagged mob drop logic
        if (tier == Tier.CARRIER && ApoConfig.carrierRequirePlayerKill) {
            if (event.source == null
                || !(event.source.getEntity() instanceof EntityPlayer)) return;
        }

        int chance;
        LootRarity rarity;
        switch (tier) {
            case BOSS:
                chance = ApoConfig.bossDropPercent;
                rarity = LootRarity.roll(rand);
                if (rarity.ordinal() < LootRarity.EPIC.ordinal()) rarity = LootRarity.EPIC;
                break;
            case ELITE:
                chance = ApoConfig.eliteDropPercent;
                rarity = LootRarity.rollCapped(rand, LootRarity.EPIC);
                if (rarity.ordinal() < LootRarity.RARE.ordinal()) rarity = LootRarity.RARE;
                break;
            case CARRIER:
                chance = ApoConfig.carrierDropPercent;
                rarity = LootRarity.rollCapped(rand, LootRarity.UNCOMMON);
                break;
            default:
                return;
        }

        if (rand.nextInt(100) >= chance) return;

        ItemStack loot = rollAffixItem(rand, rarity);
        if (loot != null) spawnDrop(entity, loot);
    }

    private void spawnDrop(EntityLivingBase entity, ItemStack stack) {
        EntityItem drop = new EntityItem(
            entity.worldObj,
            entity.posX, entity.posY, entity.posZ,
            stack
        );
        drop.delayBeforeCanPickup = 10;
        entity.worldObj.spawnEntityInWorld(drop);
    }

    /**
     * Pick a base item, roll rarity-appropriate affixes.
     *
     * Pool gated by rarity so common drops stay iron-tier and mythic drops
     * pull from diamond/enchanted gear. Easy to extend with GT-tier items later.
     */
    private ItemStack rollAffixItem(Random rand, LootRarity rarity) {
        ItemStack stack = pickBaseItem(rand, rarity);
        if (stack == null) return null;
        AffixHelper.applyRoll(stack, rarity, rand);
        return stack;
    }

    private ItemStack pickBaseItem(Random rand, LootRarity rarity) {
        int r = rand.nextInt(3); // weapon / armor / tool

        switch (rarity) {
            case COMMON:
            case UNCOMMON:
                if (r == 0) return new ItemStack(Items.iron_sword);
                if (r == 1) return new ItemStack(Items.iron_chestplate);
                return new ItemStack(Items.iron_pickaxe);

            case RARE:
                if (r == 0) return new ItemStack(Items.diamond_sword);
                if (r == 1) return new ItemStack(Items.diamond_chestplate);
                return new ItemStack(Items.diamond_pickaxe);

            case EPIC:
            case MYTHIC:
                if (r == 0) return new ItemStack(Items.diamond_sword);
                if (r == 1) return new ItemStack(Items.diamond_chestplate);
                // bow for variety at high tiers
                return new ItemStack(Items.bow);

            default:
                return new ItemStack(Items.iron_sword);
        }
    }

    // ==== ItemTooltipEvent ==================================================

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.itemStack;
        if (stack == null || !AffixHelper.hasAffixData(stack)) return;

        LootRarity rarity = AffixHelper.getRarity(stack);
        if (rarity == null) return;

        // inject rarity header above existing lore
        event.toolTip.add(1, rarity.color + "" + EnumChatFormatting.BOLD + rarity.name());

        for (Map.Entry<Affix, Integer> e : AffixHelper.getAffixes(stack).entrySet()) {
            String line = e.getKey().tooltip(stack, e.getValue());
            if (line != null) {
                event.toolTip.add(EnumChatFormatting.GRAY + line);
            }
        }
    }

    // ==== Affix firing points ==============================================

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onHurtDealt(LivingHurtEvent event) {
        DamageSource src = event.source;
        if (src == null) return;

        if (src.getEntity() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase) src.getEntity();
            ItemStack held = attacker.getHeldItem();
            if (held != null && AffixHelper.hasAffixData(held)) {
                float[] amountRef = new float[] { event.ammount };
                for (Map.Entry<Affix, Integer> e : AffixHelper.getAffixes(held).entrySet()) {
                    e.getKey().onDamageDealt(held, e.getValue(), attacker,
                                             event.entityLiving, src, amountRef);
                }
                event.ammount = amountRef[0];
            }
        }

        EntityLivingBase victim = event.entityLiving;
        if (victim != null) {
            float[] amountRef = new float[] { event.ammount };
            for (int slot = 1; slot <= 4; slot++) {
                ItemStack armor = victim.getEquipmentInSlot(slot);
                if (armor == null || !AffixHelper.hasAffixData(armor)) continue;
                for (Map.Entry<Affix, Integer> e : AffixHelper.getAffixes(armor).entrySet()) {
                    e.getKey().onDamageTaken(armor, e.getValue(), victim, src, amountRef);
                }
            }
            event.ammount = amountRef[0];
        }
    }

    @SubscribeEvent
    public void onTick(LivingUpdateEvent event) {
        EntityLivingBase entity = event.entityLiving;
        if (entity == null) return;
        for (int slot = 0; slot <= 4; slot++) {
            ItemStack stack = entity.getEquipmentInSlot(slot);
            if (stack == null || !AffixHelper.hasAffixData(stack)) continue;
            for (Map.Entry<Affix, Integer> e : AffixHelper.getAffixes(stack).entrySet()) {
                e.getKey().onTick(stack, e.getValue(), entity);
            }
        }
    }
}
