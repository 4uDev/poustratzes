package me.roxla.tasks;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.roxla.UHCMeetup;
import me.roxla.managers.GameState;
import me.roxla.utils.RandomKit;
import me.roxla.utils.Utilites;

public class PreGameTask extends BukkitRunnable {

    private final UHCMeetup plugin;
    private int timer = 30;
    private boolean forced = false;

    public PreGameTask(UHCMeetup plugin) {
        this.plugin = plugin;
    }


    @Override
    public void run() {
        if (plugin.getOnline() < 6 && !forced) {
            plugin.getGameManager().setStarted(false);
            plugin.getServer().broadcastMessage(plugin.getPrefix() + "not enough players, cancelling!");
            cancel();
        }
        if (timer == 30) {
            plugin.getServer().broadcastMessage(plugin.getPrefix() + "Scatter will begin in 15 seconds");
        }
        if (timer == 15) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 2, 2);
                plugin.getGameManager().scatterPlayer(player);
                Utilites.sitPlayer(player);
                player.setFallDistance(0);
                new RandomKit(player);
            }
        }
        if (timer <= 10) {
            if (timer == 0) {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (Utilites.getHorses().containsKey(player.getUniqueId())) {
                        Utilites.unSitPlayer(player);
                    }
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, 2, 2);
                }
                plugin.getServer().broadcastMessage(plugin.getPrefix() + "The game has begun!");
                plugin.getGameManager().setGameState(GameState.INGAME);
                plugin.getInGameTask().runTaskTimer(plugin, 0L, 20L);
                cancel();
                return;
            }
            plugin.getServer().broadcastMessage(plugin.getPrefix() + "The game will begin in " + timer + " seconds.");
        }
        timer--;
    }

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }
}
