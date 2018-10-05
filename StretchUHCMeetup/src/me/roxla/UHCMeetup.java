package me.roxla;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import me.roxla.commands.AddKitCommand;
import me.roxla.commands.ForceStartCommand;
import me.roxla.handlers.PlayerHandler;
import me.roxla.listeners.PlayerListener;
import me.roxla.managers.GameManager;
import me.roxla.managers.GameState;
import me.roxla.managers.PlayerManager;
import me.roxla.managers.WorldManager;
import me.roxla.tasks.InGameTask;
import me.roxla.tasks.PreGameTask;

public class UHCMeetup extends JavaPlugin {

    private final String prefix = ChatColor.translateAlternateColorCodes('&', "&d&lInsta&5&lPVP &8" + " \u00BB " + " &r");
    String arrow = " \u00BB ";
    private WorldManager worldManager;
    private GameManager gameManager;
    private PlayerManager playerManager;
    private PlayerHandler playerHandler;
    private PreGameTask preGameTask;
    private InGameTask inGameTask;

    @Override
    public void onEnable() {
        registerClasses();
        loadConfig();
        if (getConfig().getBoolean("SQL.enabled")) {
        }
        worldManager.generateWorld();
        registerCommand(new ForceStartCommand(this, "forcestart"));
        registerCommand(new AddKitCommand(this, "addkit"));
        new PlayerListener(this);
        saveDefaultConfig();
        registerItem();
    }

    @Override
    public void onDisable() {
        gameManager.setGameState(GameState.DOWN);
    }

    private void registerItem() {
        ItemStack ist = new ItemStack(Material.GOLDEN_APPLE, 1);
        ItemMeta im = ist.getItemMeta();
        im.setDisplayName(ChatColor.GOLD + "Golden Head");
        ShapedRecipe sr = new ShapedRecipe(ist);
        sr.shape("EEE", "ERE", "EEE");
        sr.setIngredient('E', Material.GOLD_INGOT);
        sr.setIngredient('R', Material.APPLE);
        getServer().addRecipe(sr);
    }


    private void registerClasses() {
        worldManager = new WorldManager(this);
        gameManager = new GameManager(this);
        playerManager = new PlayerManager();
        playerHandler = new PlayerHandler(this);
        preGameTask = new PreGameTask(this);
        inGameTask = new InGameTask(this);
    }

    private void registerCommand(Command command) {
        MinecraftServer.getServer().server.getCommandMap().register(command.getName(), "meetup", command);
    }

    private void loadConfig() {
        getConfig().addDefault("SQL.enabled", false);
        getConfig().addDefault("SQL.ip", "localhost");
        getConfig().addDefault("SQL.username", "username");
        getConfig().addDefault("SQL.password", "password");
        saveConfig();
    }

    public int getOnline() {
        return getServer().getOnlinePlayers().size();
    }



    public WorldManager getWorldManager() {
        return worldManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public String getPrefix() {
        return prefix;
    }

    public PlayerHandler getPlayerHandler() {
        return playerHandler;
    }

    public PreGameTask getPreGameTask() {
        return preGameTask;
    }

    public InGameTask getInGameTask() {
        return inGameTask;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
