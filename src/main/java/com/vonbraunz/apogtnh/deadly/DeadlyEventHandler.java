package com.vonbraunz.apogtnh.deadly;

import java.util.Map;
import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import org.lwjgl.input.Keyboard;

import com.vonbraunz.apogtnh.ApoConfig;
import com.vonbraunz.apogtnh.affix.Affix;
import com.vonbraunz.apogtnh.affix.AffixHelper;
import com.vonbraunz.apogtnh.affix.LootRarity;
import com.vonbraunz.apogtnh.affix.impl.AffixBeheading;
import com.vonbraunz.apogtnh.compat.TinkersItems;
import com.vonbraunz.apogtnh.deadly.DeadlyTags.Tier;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Central Forge event bus subscriber.
 *
 * Responsibilities:
 * 1. Tag mobs at spawn (boss / elite / carrier)
 * 2. Convert tags into affix drops at death (tagged mobs + regular mobs)
 * 3. Fire affix hooks on damage dealt / taken / tick
 * 4. Inject affix data into item tooltips
 */
public class DeadlyEventHandler {

    // ==== Spawn tagging =====================================================

    @SubscribeEvent
    public void onSpawn(LivingSpawnEvent event) {
        EntityLivingBase entity = event.entityLiving;
        if (!(entity instanceof IMob)) return;
        if (DeadlyTags.isTagged(entity)) return;

        Random rand = entity.worldObj.rand;

        if (ApoConfig.enableBossSpawns && ApoConfig.bossSpawnChance > 0
            && rand.nextInt(ApoConfig.bossSpawnChance) == 0) {
            DeadlyTags.tag(entity, Tier.BOSS);
            EliteMarker.applyBoss(entity);
            return;
        }

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

            LootRarity rarity = LootRarity.roll(rand);
            ItemStack loot = rollAffixItem(rand, rarity);
            if (loot != null) spawnDrop(entity, loot);
            return;
        }

        // tagged mob drop logic
        if (tier == Tier.CARRIER && ApoConfig.carrierRequirePlayerKill) {
            if (event.source == null || !(event.source.getEntity() instanceof EntityPlayer)) return;
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

        // beheading check -- runs for any mob killed by a player
        handleBeheading(event);
    }

    private void handleBeheading(LivingDropsEvent event) {
        if (!(event.source.getEntity() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.source.getEntity();
        ItemStack weapon = player.getHeldItem();
        if (weapon == null || !AffixHelper.hasAffixData(weapon)) return;

        Map<Affix, Integer> affixes = AffixHelper.getAffixes(weapon);
        AffixBeheading beh = null;
        for (Affix a : affixes.keySet()) {
            if (a instanceof AffixBeheading) {
                beh = (AffixBeheading) a;
                break;
            }
        }
        if (beh == null) return;

        int meta = skullMetaForEntity(event.entityLiving);
        if (meta < 0) return;
        int chance = beh.chancePercent(affixes.get(beh));
        if (event.entityLiving.worldObj.rand.nextInt(100) < chance) {
            spawnDrop(event.entityLiving, new ItemStack(Items.skull, 1, meta));
        }
    }

    /** map common mob types to vanilla skull metadata. -1 = no skull for this mob. */
    private static int skullMetaForEntity(EntityLivingBase entity) {
        if (entity instanceof net.minecraft.entity.monster.EntitySkeleton) {
            return ((net.minecraft.entity.monster.EntitySkeleton) entity).getSkeletonType() == 1 ? 1 : 0;
        }
        if (entity instanceof net.minecraft.entity.monster.EntityZombie) return 2;
        if (entity instanceof net.minecraft.entity.monster.EntityCreeper) return 4;
        if (entity instanceof EntityPlayer) return 3;
        return -1;
    }

    private void spawnDrop(EntityLivingBase entity, ItemStack stack) {
        EntityItem drop = new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, stack);
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
        boolean tinkers = Loader.isModLoaded("TConstruct");
        int slot = rand.nextInt(4); // 0=weapon, 1=armor, 2=tool, 3=ranged

        // armor always vanilla -- Tinkers' has no armor in 1.7.10
        if (slot == 1) return pickVanillaArmor(rand, rarity);

        // ranged stays vanilla -- Tinkers' bow assembly differs from ToolBuilder
        if (slot == 3) return pickVanillaItem(rand, rarity, slot);

        // weapons and tools route through Tinkers' if available
        if (tinkers) return pickTinkersItem(rand, rarity, slot);

        return pickVanillaItem(rand, rarity, slot);
    }

    private ItemStack pickVanillaArmor(Random rand, LootRarity rarity) {
        switch (rarity) {
            case COMMON:
            case UNCOMMON:
                return pickRandomArmor(
                    rand,
                    Items.iron_helmet,
                    Items.iron_chestplate,
                    Items.iron_leggings,
                    Items.iron_boots);
            case RARE:
                return enchantArmor(
                    pickRandomArmor(
                        rand,
                        Items.diamond_helmet,
                        Items.diamond_chestplate,
                        Items.diamond_leggings,
                        Items.diamond_boots),
                    rand,
                    5,
                    15);
            case EPIC:
            case MYTHIC: {
                int maxEnch = (rarity == LootRarity.MYTHIC) ? 25 : 15;
                int minEnch = (rarity == LootRarity.MYTHIC) ? 15 : 5;
                return enchantArmor(
                    pickRandomArmor(
                        rand,
                        Items.diamond_helmet,
                        Items.diamond_chestplate,
                        Items.diamond_leggings,
                        Items.diamond_boots),
                    rand,
                    minEnch,
                    maxEnch);
            }
            default:
                return pickRandomArmor(
                    rand,
                    Items.iron_helmet,
                    Items.iron_chestplate,
                    Items.iron_leggings,
                    Items.iron_boots);
        }
    }

    private ItemStack pickTinkersItem(Random rand, LootRarity rarity, int slot) {
        // slot 0=weapon, 2=tool, 3=ranged (armor already handled separately)
        switch (rarity) {
            case COMMON:
                return TinkersItems.createPart(rand, 0);
            case UNCOMMON:
                return TinkersItems.createPart(rand, 1);
            case RARE:
                return TinkersItems.createPart(rand, 2);
            case EPIC:
                return TinkersItems.createTool(rand, false);
            case MYTHIC:
                return TinkersItems.createTool(rand, true);
            default:
                return TinkersItems.createPart(rand, 0);
        }
    }

    private ItemStack pickVanillaItem(Random rand, LootRarity rarity, int slot) {
        switch (rarity) {
            case COMMON:
            case UNCOMMON:
                switch (slot) {
                    case 0:
                        return rand.nextBoolean() ? new ItemStack(Items.iron_sword) : new ItemStack(Items.iron_axe);
                    case 2:
                        return rand.nextBoolean() ? new ItemStack(Items.iron_pickaxe)
                            : new ItemStack(Items.iron_shovel);
                    default:
                        return new ItemStack(Items.bow);
                }
            case RARE:
                switch (slot) {
                    case 0:
                        return rand.nextBoolean() ? new ItemStack(Items.diamond_sword)
                            : new ItemStack(Items.diamond_axe);
                    case 2:
                        return rand.nextBoolean() ? new ItemStack(Items.diamond_pickaxe)
                            : new ItemStack(Items.diamond_shovel);
                    default:
                        return new ItemStack(Items.bow);
                }
            case EPIC:
            case MYTHIC: {
                int maxEnch = (rarity == LootRarity.MYTHIC) ? 25 : 15;
                int minEnch = (rarity == LootRarity.MYTHIC) ? 15 : 5;
                switch (slot) {
                    case 0: {
                        ItemStack s = new ItemStack(Items.diamond_sword);
                        s.addEnchantment(Enchantment.sharpness, minEnch + rand.nextInt(maxEnch - minEnch + 1));
                        s.addEnchantment(Enchantment.unbreaking, minEnch + rand.nextInt(maxEnch - minEnch + 1));
                        return s;
                    }
                    case 2: {
                        ItemStack t = new ItemStack(Items.diamond_pickaxe);
                        t.addEnchantment(Enchantment.efficiency, minEnch + rand.nextInt(maxEnch - minEnch + 1));
                        t.addEnchantment(Enchantment.fortune, minEnch + rand.nextInt(maxEnch - minEnch + 1));
                        t.addEnchantment(Enchantment.unbreaking, minEnch + rand.nextInt(maxEnch - minEnch + 1));
                        return t;
                    }
                    default: {
                        ItemStack b = new ItemStack(Items.bow);
                        b.addEnchantment(Enchantment.power, minEnch + rand.nextInt(maxEnch - minEnch + 1));
                        b.addEnchantment(Enchantment.flame, minEnch + rand.nextInt(maxEnch - minEnch + 1));
                        return b;
                    }
                }
            }
            default:
                return new ItemStack(Items.iron_sword);
        }
    }

    private ItemStack pickRandomArmor(Random rand, Item helmet, Item chest, Item legs, Item boots) {
        int r = rand.nextInt(4);
        switch (r) {
            case 0:
                return new ItemStack(helmet);
            case 1:
                return new ItemStack(chest);
            case 2:
                return new ItemStack(legs);
            default:
                return new ItemStack(boots);
        }
    }

    private ItemStack enchantArmor(ItemStack stack, Random rand, int minEnch, int maxEnch) {
        stack.addEnchantment(Enchantment.protection, minEnch + rand.nextInt(maxEnch - minEnch + 1));
        stack.addEnchantment(Enchantment.unbreaking, minEnch + rand.nextInt(maxEnch - minEnch + 1));
        return stack;
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

        for (Map.Entry<Affix, Integer> e : AffixHelper.getAffixes(stack)
            .entrySet()) {
            String line = e.getKey()
                .tooltip(stack, e.getValue());
            if (line != null) {
                event.toolTip.add(EnumChatFormatting.GRAY + line);
            }
            // shift-held detailed description
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                String desc = e.getKey()
                    .description(e.getValue());
                if (desc != null) {
                    event.toolTip.add(EnumChatFormatting.DARK_GRAY + "  " + desc);
                }
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
                for (Map.Entry<Affix, Integer> e : AffixHelper.getAffixes(held)
                    .entrySet()) {
                    e.getKey()
                        .onDamageDealt(held, e.getValue(), attacker, event.entityLiving, src, amountRef);
                }
                event.ammount = amountRef[0];
            }
        }

        // arrow affixes: read the bow's stashed affix NBT from the arrow entity
        if (src.getSourceOfDamage() instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow) src.getSourceOfDamage();
            NBTTagCompound apo = arrow.getEntityData()
                .getCompoundTag("apogtnh");
            if (apo.hasKey("affixes", 10)) {
                NBTTagCompound affixes = apo.getCompoundTag("affixes");
                float[] amountRef = new float[] { event.ammount };

                // surgical: bonus critical multiplier per level
                if (affixes.hasKey("apogtnh:surgical")) {
                    amountRef[0] *= 1.0F + affixes.getInteger("apogtnh:surgical") * 0.25F;
                }
                // piercing: flat bonus damage per level
                if (affixes.hasKey("apogtnh:piercing")) {
                    amountRef[0] += affixes.getInteger("apogtnh:piercing") * 2.0F;
                }
                // explosive: TNT blast on impact
                if (affixes.hasKey("apogtnh:explosive")) {
                    event.entityLiving.worldObj.createExplosion(
                        null,
                        event.entityLiving.posX,
                        event.entityLiving.posY,
                        event.entityLiving.posZ,
                        2.0F,
                        true);
                }
                // venom: poison on hit, duration scales with level
                if (affixes.hasKey("apogtnh:venom")) {
                    int level = affixes.getInteger("apogtnh:venom");
                    event.entityLiving.addPotionEffect(new PotionEffect(Potion.poison.id, 60 + level * 20, level - 1));
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
                for (Map.Entry<Affix, Integer> e : AffixHelper.getAffixes(armor)
                    .entrySet()) {
                    e.getKey()
                        .onDamageTaken(armor, e.getValue(), victim, src, amountRef);
                }
            }
            event.ammount = amountRef[0];
        }
    }

    @SubscribeEvent
    public void onTick(LivingUpdateEvent event) {
        EntityLivingBase entity = event.entityLiving;
        if (entity == null) return;

        // reset mutable state that affix onTick handlers may override
        if (entity instanceof EntityPlayer) {
            entity.stepHeight = 0.5F; // vanilla default -- AffixStepAssist will bump it
        }

        for (int slot = 0; slot <= 4; slot++) {
            ItemStack stack = entity.getEquipmentInSlot(slot);
            if (stack == null || !AffixHelper.hasAffixData(stack)) continue;
            for (Map.Entry<Affix, Integer> e : AffixHelper.getAffixes(stack)
                .entrySet()) {
                e.getKey()
                    .onTick(stack, e.getValue(), entity);
            }
        }
    }
}
