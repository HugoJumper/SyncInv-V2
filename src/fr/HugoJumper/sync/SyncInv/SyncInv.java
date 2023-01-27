package fr.HugoJumper.sync.SyncInv;

import java.sql.SQLException;
import fr.HugoJumper.sync.listerner.PlayerListener;
import fr.HugoJumper.sync.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import fr.HugoJumper.sync.database.DataBase;
import fr.HugoJumper.sync.command.SaveKickCommand;
import fr.HugoJumper.sync.database.manager.DataBaseManager;

public class SyncInv  extends JavaPlugin {
    private DataBase database;
    private DataBaseManager databasemanager;
    private ConfigManager configManager;

    private ConsoleCommandSender consoleSender;

    public void onEnable() {
        saveDefaultConfig();
        this.database = new DataBase(getConfig());
        this.consoleSender = getServer().getConsoleSender();
        registerDb();
        registerManagers();
        registerCommands();
        for (Player player : Bukkit.getServer().getOnlinePlayers())
            getDatabasemanager().loadPlayer(player);
    }

    public void onDisable() {
        for (Player player : Bukkit.getServer().getOnlinePlayers())
            getDatabasemanager().savePlayer(player);
        if (this.database.isConnected())
            this.database.disconnect();
    }

    private void registerManagers() {
        this.databasemanager = new DataBaseManager(this);
        this.configManager = new ConfigManager(this);
    }

    private void registerCommands() {
        new SaveKickCommand(this);
    }

    private void registerManager(){
        getServer().getPluginManager().registerEvents((Listener) new PlayerListener(this), (Plugin) this);
    }
    private void registerDb() {
        try{
        this.database.connect();
        this.getConsoleSender().sendMessage(ChatColor.YELLOW + " ========== Connection en cours =========");
        this.getConsoleSender().sendMessage(ChatColor.AQUA + " Base de données Connecte !");
        this.getConsoleSender().sendMessage(ChatColor.YELLOW + " ==========================================");
    }
        catch(SQLException exception){
        this.getConsoleSender().sendMessage(ChatColor.YELLOW + " ========== Connection SQL echouer =========");
        this.getConsoleSender().sendMessage(ChatColor.RED + " Your SQL has to be checked in your config.yml");
        this.getConsoleSender().sendMessage(ChatColor.RED + " Plugin désactiver...");
        this.getConsoleSender().sendMessage(ChatColor.YELLOW + " ==========================================");
        exception.printStackTrace();
        getServer().getPluginManager().disablePlugin((Plugin) this);
    }
}

    public DataBase getDb() {
        return this.database;
    }

    public DataBaseManager getDatabasemanager() {
        return this.databasemanager;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public ConsoleCommandSender getConsoleSender() {
        return this.consoleSender;
    }
}