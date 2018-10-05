package me.roxla.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.roxla.UHCMeetup;
import me.roxla.managers.GameState;
import me.roxla.player.UHCPlayer;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener {

    private final UHCMeetup plugin;
    private List<Block> playerBlocks;

    public PlayerListener(UHCMeetup plugin) {
        this.plugin = plugin;
        playerBlocks = new ArrayList<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        GameState state = plugin.getGameManager().getGameState();
        if (state == GameState.END || state == GameState.PREPARING || state == GameState.DOWN) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You may not join in this state.");
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("uhc.whitelist") && plugin.getGameManager().getGameState() == GameState.INGAME || plugin.getGameManager().getGameState() == GameState.SCATTERING) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "cThis game has already started! Buy a donator rank to spectate!");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage("");
        if (!plugin.getPlayerManager().getPlayerMap().containsKey(player.getUniqueId())) {
            plugin.getPlayerManager().addToMap(player);
        }
        plugin.getPlayerHandler().handlePlayer(plugin.getPlayerManager().getPlayerMap().get(player.getUniqueId()));
        
        if(plugin.getGameManager().getGameState().equals(GameState.PREPARING) && player.getWorld().getName() == "game_world"){
        	player.teleport(Bukkit.getWorld("spawn").getSpawnLocation());
        }

        if (plugin.getOnline() >= 6 && !plugin.getGameManager().isStarted()) {
            plugin.getGameManager().setStarted(true);
            plugin.getPreGameTask().runTaskTimer(plugin, 0L, 20L);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UHCPlayer quitter = plugin.getPlayerManager().getPlayerMap().get(player.getUniqueId());
        if (plugin.getGameManager().isStarted() && quitter.isAlive()) {
            event.setQuitMessage(ChatColor.RED + quitter.getName() + " has disconnected!");
            World world = player.getWorld();
            for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                if (itemStack.getType() == Material.AIR) continue;
                world.dropItemNaturally(player.getLocation(), itemStack);
            }
            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack.getType() == Material.AIR) continue;
                world.dropItemNaturally(player.getLocation(), itemStack);
            }
            quitter.setAlive(false);
            plugin.getGameManager().checkForWin();
            return;
        }
        plugin.getPlayerManager().removeFromMap(event.getPlayer().getUniqueId());
        event.setQuitMessage("");
    }


    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UHCPlayer deather = plugin.getPlayerManager().getPlayerMap().get(player.getUniqueId());

        String deathMessage = ChatColor.RED + event.getDeathMessage();
        deathMessage = deathMessage.replace(player.getName(), ChatColor.RED + player.getName() + ChatColor.WHITE + "[" + deather.getKills() + "]" + ChatColor.RED);
        Player killer = event.getEntity().getKiller();
        deather.setAlive(false);
        if (killer != null) {
            UHCPlayer uhcKiller = plugin.getPlayerManager().getPlayerMap().get(killer.getUniqueId());
            uhcKiller.setKills(uhcKiller.getKills() + 1);
            deathMessage = deathMessage.replace(player.getKiller().getName(), ChatColor.RED + killer.getName() + ChatColor.WHITE + "[" + uhcKiller.getKills() + "]" + ChatColor.RED);
        }
        plugin.getPlayerHandler().handlePlayer(deather);
        //not setting death message bc something weird was happening
        plugin.getServer().broadcastMessage(deathMessage);
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getGameManager().checkForWin();
            }
        }.runTask(plugin);
        event.setDeathMessage(null);
    }


    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (plugin.getGameManager().getGameState() == GameState.SCATTERING) {
            event.setCancelled(true);
            return;
        }

        if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Golden Head")) {
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 2));
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
    	UHCPlayer uhcPlayer = plugin.getPlayerManager().getPlayerMap().get(event.getPlayer().getUniqueId());
        if (!uhcPlayer.isAlive()) {
            event.setCancelled(true);
            return;
        }
        if (plugin.getGameManager().getGameState() == GameState.SCATTERING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEmptyBucket(PlayerBucketEmptyEvent event) {
    	UHCPlayer uhcPlayer = plugin.getPlayerManager().getPlayerMap().get(event.getPlayer().getUniqueId());
        if (!uhcPlayer.isAlive()) {
            event.setCancelled(true);
            return;
        }
    	
        if (plugin.getGameManager().getGameState() == GameState.SCATTERING) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
    	if (event.getEntity().getWorld().getName().equalsIgnoreCase("spawn") && event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            event.getEntity().teleport(plugin.getServer().getWorld("spawn").getSpawnLocation().add(.5, .5, .5));
        }
        if (plugin.getGameManager().getGameState() != GameState.INGAME) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onCreative(InventoryCreativeEvent event) {
        if (plugin.getGameManager().getGameState() != GameState.READY) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equalsIgnoreCase("spawn") && player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            return;
        }
    	UHCPlayer uhcPlayer = plugin.getPlayerManager().getPlayerMap().get(event.getPlayer().getUniqueId());
        if (!uhcPlayer.isAlive()) {
            event.setCancelled(true);
            return;
        }
        if (plugin.getGameManager().getGameState() == GameState.SCATTERING) {
            event.setCancelled(true);
            return;
        }
        playerBlocks.add(event.getBlockPlaced());
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equalsIgnoreCase("spawn") && player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            return;
        }
    	UHCPlayer uhcPlayer = plugin.getPlayerManager().getPlayerMap().get(event.getPlayer().getUniqueId());
        if (!uhcPlayer.isAlive()) {
            event.setCancelled(true);
            return;
        }
        if (plugin.getGameManager().getGameState() == GameState.SCATTERING) {
            event.setCancelled(true);
            return;
        }
        if (!playerBlocks.contains(event.getBlock())) {
            player.sendMessage(ChatColor.RED + "You may only break blocks placed by players.");
            event.setCancelled(true);
        }
    }
}
