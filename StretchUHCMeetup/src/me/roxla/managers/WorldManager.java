package me.roxla.managers;

import net.minecraft.server.v1_8_R3.BiomeBase;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.roxla.UHCMeetup;

import java.io.IOException;
import java.lang.reflect.Field;

public class WorldManager {


    private final UHCMeetup plugin;
    private World uhcWorld;

    public WorldManager(UHCMeetup plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onChunkLoad(ChunkLoadEvent event) {
                Chunk chunk = event.getChunk();
                if(chunk.getBlock(chunk.getX(), 0, chunk.getZ()).getBiome() != Biome.PLAINS){
                    replacechunks();
                }
            }
        }, plugin);
    }


    public void generateWorld() {
        World world = plugin.getServer().getWorld("game_world");
        if (world == null) {
            WorldCreator creator = new WorldCreator("game_world");
            creator.environment(World.Environment.NORMAL);
            creator.type(WorldType.NORMAL);
            creator.generateStructures(false);
            world = plugin.getServer().createWorld(creator);
            world.setDifficulty(Difficulty.PEACEFUL);
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("naturalRegeneration", "false");
            world.setSpawnLocation(0, 100, 0);
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getGameManager().setBorderSize(100, 0);
                }
            }.runTaskLater(plugin, 20 * 10);
        }else{
        	deleteWorld();
        }
        uhcWorld = world;
        plugin.getGameManager().setGameState(GameState.READY);
    }

    public void deleteWorld() {
        plugin.getServer().unloadWorld(uhcWorld, true);
        try {
            FileUtils.deleteDirectory(uhcWorld.getWorldFolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void replacechunks(){
        Field biomesField = null;
        try {
            biomesField = BiomeBase.class.getDeclaredField("biomes");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        assert biomesField != null;
        biomesField.setAccessible(true);
        try {
            if (biomesField.get(null) instanceof BiomeBase[]) {
                BiomeBase[] biomes = (BiomeBase[]) biomesField.get(null);
                biomes[BiomeBase.BEACH.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.BIRCH_FOREST.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.BIRCH_FOREST_HILLS.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.COLD_BEACH.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.COLD_TAIGA.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.COLD_TAIGA_HILLS.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.DEEP_OCEAN.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.DESERT.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.DESERT_HILLS.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.EXTREME_HILLS.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.EXTREME_HILLS_PLUS.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.FOREST.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.FOREST_HILLS.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.FROZEN_OCEAN.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.FROZEN_RIVER.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.ICE_MOUNTAINS.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.ICE_PLAINS.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.JUNGLE.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.JUNGLE_EDGE.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.JUNGLE_HILLS.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.MEGA_TAIGA.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.MEGA_TAIGA_HILLS.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.MESA.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.MESA_PLATEAU.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.MESA_PLATEAU_F.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.MUSHROOM_ISLAND.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.MUSHROOM_SHORE.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.OCEAN.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.RIVER.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.ROOFED_FOREST.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.SAVANNA.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.SAVANNA_PLATEAU.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.SMALL_MOUNTAINS.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.STONE_BEACH.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.SWAMPLAND.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.TAIGA.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.TAIGA_HILLS.id] = BiomeBase.PLAINS;
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public World getUhcWorld() {
        return uhcWorld;
    }
}
