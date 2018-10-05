package me.roxla.managers;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.roxla.UHCMeetup;
import me.roxla.player.UHCPlayer;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class GameManager {

    private final UHCMeetup plugin;
    private GameState gameState = GameState.PREPARING;
    private int borderSize = 100;
    private boolean started = false;
    private boolean shrinking = false;

    public GameManager(UHCMeetup plugin) {
        this.plugin = plugin;
    }


    private void endGame(Collection<UHCPlayer> winners) {
        setGameState(GameState.END);
        int kills = 0;
        StringBuilder builder = new StringBuilder(ChatColor.RED + "Congratulations to ");
        if (winners != null) {
            for (UHCPlayer player : winners) {
                kills += player.getKills();
                builder.append(player.getName()).append(", ");
            }
        } else {
            builder.append("N/A, ");
        }
        plugin.getServer().broadcastMessage(builder.toString().substring(0, builder.length() - 2) + " on winning this meetup with a total of " + kills + " kills!");
        plugin.getServer().broadcastMessage(plugin.getPrefix() + "the server will reboot in 15 seconds!");
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    player.kickPlayer(ChatColor.RED + "Thanks for playing!");
                }
                for (Entity entity : plugin.getWorldManager().getUhcWorld().getLivingEntities()) {
                    entity.remove();
                }
                plugin.getWorldManager().deleteWorld();
                plugin.getWorldManager().generateWorld();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "restart");
                    }
                }.runTaskLater(plugin, 20 * 5);
            }
        }.runTaskLater(plugin, 20 * 15);
    }


    public void scatterPlayer(Player player) {
        int x = ThreadLocalRandom.current().nextInt(0, borderSize * 2) - borderSize;
        int z = ThreadLocalRandom.current().nextInt(0, borderSize* 2) - borderSize;
        Location location = new Location(plugin.getWorldManager().getUhcWorld(), x, (plugin.getWorldManager().getUhcWorld().getHighestBlockYAt(x, z) + 0.5D), z);
        player.teleport(location);
    }

    public void checkForWin() {
        if (gameState == GameState.INGAME) {
            if (plugin.getPlayerManager().getPlayerMap().values().stream().noneMatch(UHCPlayer::isAlive)) {
                endGame(null);
            }
            if (plugin.getPlayerManager().getPlayerMap().values().stream().filter(UHCPlayer::isAlive).count() == 1) {
                Optional<UHCPlayer> player = plugin.getPlayerManager().getPlayerMap().values().stream().filter(UHCPlayer::isAlive).findAny();
                if (player.isPresent()) {
                    endGame(Collections.singleton(player.get()));
                } else {
                    endGame(null);
                }
            }
        }
    }
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getBorderSize() {
        return borderSize;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public void setBorderSize(int size, int timeToShrink) {
        if (timeToShrink == 0) {
            genWalls(size);
            return;
        }
        shrinking = true;
        new BukkitRunnable() {
            int remaining = timeToShrink;
            @Override
            public void run() {
                if (remaining == 60 || remaining == 30 || remaining == 15 || remaining <= 10) {
                    plugin.getServer().broadcastMessage(plugin.getPrefix() + "The border will shrink in " + remaining + " seconds.");
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 2, 2);
                    }
                }
                if (remaining == 0) {
                    genWalls(size);
                    shrinking = false;
                    cancel();
                }
                remaining--;
            }
        }.runTaskTimer(plugin, 0L, 20);
        this.borderSize = size;
    }

    private void genWalls(int size) {
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "wb " + plugin.getWorldManager().getUhcWorld().getName() + " set " + size + " " + size + " 0 0");
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "wb shape square");
        Location loc = new Location(plugin.getWorldManager().getUhcWorld(), 0.0D, 59.0D, 0.0D);
        World uhc = plugin.getWorldManager().getUhcWorld();
        int i = 4;
        while (i < 4 + 4)
        {
            for (int x = loc.getBlockX() - size; x <= loc.getBlockX() + size; x++) {
                for (int y = 59; y <= 59; y++) {
                    for (int z = loc.getBlockZ() - size; z <= loc.getBlockZ() + size; z++) {
                        if ((x == loc.getBlockX() - size) || (x == loc.getBlockX() + size) || (z == loc.getBlockZ() - size) || (z == loc.getBlockZ() + size)){
                            Location loc2 = new Location(plugin.getWorldManager().getUhcWorld(), x, y, z);
                            loc2.setY(uhc.getHighestBlockYAt(loc2));
                            loc2.getBlock().setType(Material.BEDROCK);
                        }
                    }
                }
            }
            i++;
        }

    }

    public boolean isShrinking() {
        return shrinking;
    }
}
