package me.roxla.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.roxla.UHCMeetup;

public class ForceStartCommand extends Command {

    private final UHCMeetup plugin;

    public ForceStartCommand(UHCMeetup plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {

        if (!commandSender.isOp()) return true;
        plugin.getServer().broadcastMessage(plugin.getPrefix() + commandSender.getName() + " has force started the game.");
        plugin.getPreGameTask().setForced(true);
        plugin.getGameManager().setStarted(true);
        plugin.getPreGameTask().runTaskTimer(plugin, 0L, 20L);
        return false;
    }
}
