package me.roxla.player;

import org.bukkit.entity.Player;

import java.util.UUID;

public class UHCPlayer {

    private final UUID uuid;
    private final String name;
    private int kills = 0;
    private boolean alive = false;
    private boolean spectator = false;

    public UHCPlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getDisplayName();
    }


    public int getKills() {
        return kills;
    }

    public boolean isSpectator(){
    	return spectator;
    }
    
    
    
    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }
    
    public void makeSpectator(boolean spectator){
    	this.spectator = spectator;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
