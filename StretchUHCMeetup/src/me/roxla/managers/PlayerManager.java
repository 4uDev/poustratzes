package me.roxla.managers;

import org.bukkit.entity.Player;

import me.roxla.player.UHCPlayer;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {

    private final HashMap<UUID, UHCPlayer> playerMap;

    public PlayerManager() {
        this.playerMap = new HashMap<>();
    }

    public HashMap<UUID, UHCPlayer> getPlayerMap() {
        return playerMap;
    }

    public void removeFromMap(UUID uuid) {
        playerMap.remove(uuid);
    }

    public void addToMap(Player player) {
        playerMap.put(player.getUniqueId(), new UHCPlayer(player));
    }
    
    public long getSpectators() {
    	return getPlayerMap().values().stream().filter(UHCPlayer::isSpectator).count();
    }
    
    public long getAmountAlive() {
        return getPlayerMap().values().stream().filter(UHCPlayer::isAlive).count();
    }
}
