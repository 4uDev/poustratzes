package me.roxla.utils;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Utilites {
    private static Map<UUID, Integer> horses = new HashMap<>();


    //I found this on an open source UHC plugin on github
    //https://github.com/KrizzDawg/UltraUhc
    //credit goes to whoever made it
    public static void sitPlayer(Player p) {
        Location l = p.getLocation();
        EntityHorse horse = new EntityHorse(((CraftWorld) l.getWorld()).getHandle());
        EntityCreeper creeper = new EntityCreeper(((CraftWorld) l.getWorld()).getHandle());
        creeper.setLocation(l.getX(), l.getY(), l.getZ(), 0.0f, 0.0f);
        creeper.setInvisible(true);
        horse.setLocation(l.getX(), l.getY(), l.getZ(), 0.0f, 0.0f);
        horse.setInvisible(true);
        final PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(creeper);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        horses.put(p.getUniqueId(), creeper.getId());
        final PacketPlayOutAttachEntity sit = new PacketPlayOutAttachEntity(0,((CraftPlayer) p).getHandle(), creeper);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(sit);
    }

    public static void unSitPlayer(final Player p) {

        if (!horses.containsKey(p.getUniqueId())) return;
        final PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(horses.get(p.getUniqueId()));
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        horses.remove(p.getUniqueId());
    }

    public static Map<UUID, Integer> getHorses() {
        return horses;
    }
}
