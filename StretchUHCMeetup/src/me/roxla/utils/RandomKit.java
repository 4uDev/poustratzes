package me.roxla.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomKit {

    private static final List<ItemStack[]> armor = new ArrayList<>();
    private static final List<ItemStack[]> kits = new ArrayList<>();


    static {
        armor.add(new ItemStack[]{
                createItem(Material.DIAMOND_HELMET, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1),
                createItem(Material.DIAMOND_CHESTPLATE, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1),
                createItem(Material.DIAMOND_LEGGINGS, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1),
                createItem(Material.DIAMOND_BOOTS, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1)
        });

        kits.add(new ItemStack[]{
                createItem(Material.IRON_SWORD, 1, Enchantment.DAMAGE_ALL, 3),
                createItem(Material.LAVA_BUCKET, 1),
                createItem(Material.FISHING_ROD, 1),
                createItem(Material.BOW, 1, Enchantment.ARROW_DAMAGE, 1),
                createItem(Material.COBBLESTONE, 64),
                createItem(Material.COOKED_BEEF, 32),
                createItem(Material.DIAMOND_PICKAXE, 1),
                createItem(Material.GOLDEN_APPLE, 5),
                createItem(Material.GOLDEN_APPLE, 3, ChatColor.GOLD + "Golden Head"),
                createItem(Material.WATER_BUCKET, 1),
                createItem(Material.WATER_BUCKET, 1),
                createItem(Material.ARROW, 32),
                createItem(Material.LAVA_BUCKET, 1),
                createItem(Material.ENCHANTMENT_TABLE, 1),
                createItem(Material.ANVIL, 1),
                createItem(Material.DIAMOND_AXE, 1),
                createItem(Material.EXP_BOTTLE, 16),
                createItem(Material.WOOD, 64),
        });
    }

    private static ItemStack createItem(Material material, int itemAmount, Enchantment enchantment, int enchantmentLevel) {
        ItemStack item = new ItemStack(material, itemAmount);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addEnchant(enchantment, enchantmentLevel, true);
        item.setItemMeta(itemMeta);
        return item;
    }

    private static ItemStack createItem(Material material, int itemAmount, String name) {
        ItemStack item = new ItemStack(material, itemAmount);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        return item;
    }

    private static ItemStack createItem(Material material, int itemAmount) {
        return new ItemStack(material, itemAmount);
    }

    public RandomKit(Player player) {
        int random = ThreadLocalRandom.current().nextInt(0, 15);
        player.getInventory().setHelmet(armor.get(random)[0]);
        player.getInventory().setChestplate(armor.get(random)[1]);
        player.getInventory().setLeggings(armor.get(random)[2]);
        player.getInventory().setBoots(armor.get(random)[3]);
        player.getInventory().setArmorContents(armor.get(random));
        player.getInventory().setContents(kits.get(random));
    }
}
