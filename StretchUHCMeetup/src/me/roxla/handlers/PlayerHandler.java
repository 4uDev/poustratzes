package me.roxla.handlers;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import me.roxla.UHCMeetup;
import me.roxla.managers.GameState;
import me.roxla.player.UHCPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerHandler {

    private final UHCMeetup plugin;
    private final Set<UUID> vanished;
    public PlayerHandler(UHCMeetup plugin) {
        this.plugin = plugin;
        vanished = new HashSet<>();
    }


    private void toggleVisiblePlayer(Player player) {
        vanished.add(player.getUniqueId());
        player.spigot().setCollidesWithEntities(false);
        for (Player online : plugin.getServer().getOnlinePlayers()) {
            if (vanished.contains(online.getUniqueId())) continue;
            online.hidePlayer(player);
        }
        for (UUID uuid : vanished) {
            Player player1 = Bukkit.getServer().getPlayer(uuid);
            if (player1 != null) {
                player.showPlayer(player1);
            }
        }
    }

    public void handlePlayer(UHCPlayer player) {
        Player player1 = plugin.getServer().getPlayer(player.getUuid());

        if (plugin.getGameManager().getGameState() == GameState.READY) {
            player.setAlive(true);
            player1.setHealth(20D);
            player1.setFoodLevel(20);
            player1.setExp(0);
            player1.setTotalExperience(0);
            player1.setLevel(0);
            player1.setGameMode(GameMode.SURVIVAL);
            player1.getInventory().clear();
            player1.getInventory().setArmorContents(null);
            player1.teleport(plugin.getServer().getWorld("spawn").getSpawnLocation().add(.5, .5, .5));
            plugin.getServer().broadcastMessage(plugin.getPrefix() + player1.getDisplayName() + " has joined the game. (" + plugin.getOnline() + "/16)");
        } else {
            toggleVisiblePlayer(player1);
            player1.teleport(plugin.getWorldManager().getUhcWorld().getSpawnLocation());
            //todo give spec items
            player1.setGameMode(GameMode.CREATIVE);
            player1.sendMessage(plugin.getPrefix() + "You are now a spectator.");
        }
    }
}
