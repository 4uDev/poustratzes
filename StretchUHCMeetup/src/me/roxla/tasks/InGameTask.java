package me.roxla.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import me.roxla.UHCMeetup;

public class InGameTask extends BukkitRunnable {

    private final UHCMeetup plugin;
    private int time = 0;

    public InGameTask(UHCMeetup plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (plugin.getPlayerManager().getAmountAlive() == 4 && !plugin.getGameManager().isShrinking() && plugin.getGameManager().getBorderSize() != 50) {
            plugin.getGameManager().setBorderSize(50, 10);
        }
        if (plugin.getPlayerManager().getAmountAlive() == 2 && !plugin.getGameManager().isShrinking() && plugin.getGameManager().getBorderSize() != 25) {
            plugin.getGameManager().setBorderSize(25, 10);
        }

        if (time % 60 == 3 && !plugin.getGameManager().isShrinking() && plugin.getGameManager().getBorderSize() <= 50) {
            plugin.getGameManager().setBorderSize(50, 10);
        }

        if (time % 60 == 7.5 && !plugin.getGameManager().isShrinking() && plugin.getGameManager().getBorderSize() <= 25) {
            plugin.getGameManager().setBorderSize(50, 10);
        }

        time++;
    }

    public int getTime() {
        return time;
    }
}
