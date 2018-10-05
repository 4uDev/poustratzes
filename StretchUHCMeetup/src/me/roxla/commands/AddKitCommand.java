package me.roxla.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.roxla.UHCMeetup;

import java.util.UUID;

public class AddKitCommand extends Command {

    private final UHCMeetup plugin;

    public AddKitCommand(UHCMeetup plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!sender.isOp() && !(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (args.length != 0) {
            if (args[0].equalsIgnoreCase("gapple")){
                player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE) {
                    @Override
                    public boolean setItemMeta(ItemMeta itemMeta) {
                        itemMeta.setDisplayName(ChatColor.GOLD + "Golden Head");
                        return super.setItemMeta(itemMeta);
                    }
                });
            } else if (args[0].equalsIgnoreCase("save")){
                UUID uuid = UUID.randomUUID();
                plugin.getConfig().set("kits.inv." + uuid, player.getInventory().getContents());
                plugin.getConfig().set("kits.armor." + uuid, player.getInventory().getArmorContents());
                plugin.saveConfig();
                player.sendMessage(ChatColor.GREEN + "Kit saved!");
            } else {

            }
        }


        return false;
    }
}
