package com.vonbraunz.apogtnh.deadly;

import java.util.Random;

import com.vonbraunz.apogtnh.ApoConfig;
import com.vonbraunz.apogtnh.deadly.DeadlyTags.Tier;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

/**
 * Chunk-populate boss placement.
 *
 * Fires once per chunk during world gen. Rolls chance, finds a valid surface tile,
 * spawns a boss-tier hostile mob and tags it.
 *
 * TODO(scaffold): richer boss selection (biome-aware, day/night check, mob type variety,
 * skeleton/creeper/spider bosses, etc.). Starter just spawns a zombie boss.
 */
public class BossPlacer {

    @SubscribeEvent
    public void onPopulate(PopulateChunkEvent.Post event) {
        if (!ApoConfig.enableBossSpawns) return;
        if (ApoConfig.bossChunkChance <= 0) return;

        World world = event.world;
        if (world.provider.dimensionId != 0) return;   // overworld only for now

        Random rand = event.rand;
        if (rand.nextInt(ApoConfig.bossChunkChance) != 0) return;

        int cx = event.chunkX * 16 + rand.nextInt(16);
        int cz = event.chunkZ * 16 + rand.nextInt(16);
        int cy = world.getHeightValue(cx, cz);
        if (cy < 40 || cy > 120) return;               // avoid caves and mountain peaks

        // Biome guard: skip water/deserts if you want. Start permissive.
        BiomeGenBase biome = world.getBiomeGenForCoords(cx, cz);
        if (biome == BiomeGenBase.ocean || biome == BiomeGenBase.deepOcean) return;

        EntityZombie boss = new EntityZombie(world);
        boss.setLocationAndAngles(cx + 0.5D, cy, cz + 0.5D, rand.nextFloat() * 360F, 0F);
        if (!world.spawnEntityInWorld(boss)) return;

        DeadlyTags.tag(boss, Tier.BOSS);
        EliteMarker.applyBoss(boss);

        // prevent despawning so bosses persist like named mobs
        boss.func_110163_bv(); // enablePersistence — func name from MCP mappings
    }
}
