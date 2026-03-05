package net.silentium.engine;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import me.xflyiwnl.colorfulgui.ColorfulGUI;
import net.luckperms.api.LuckPerms;
import net.silentium.engine.api.config.FileManager;
import net.silentium.engine.api.config.YAML;
import net.silentium.engine.api.cooldown.CooldownService;
import net.silentium.engine.api.database.Database;
import net.silentium.engine.api.user.UserDAO;
import net.silentium.engine.core.command.SEngineCommand;
import net.silentium.engine.core.cooldown.CooldownListener;
import net.silentium.engine.core.item.ItemLevelingManager;
import net.silentium.engine.core.item.ItemManager;
import net.silentium.engine.core.item.ItemXPListener;
import net.silentium.engine.core.item.command.GiveItemCommand;
import net.silentium.engine.core.lang.LanguageManager;
import net.silentium.engine.core.lang.command.LanguageCommand;
import net.silentium.engine.core.lang.packet.LanguagePacketListener;
import net.silentium.engine.core.menus.MenuManager;
import net.silentium.engine.core.scoreboard.ScoreboardListener;
import net.silentium.engine.internal.cooldown.CooldownDAO;
import net.silentium.engine.internal.cooldown.CooldownDAOImpl;
import net.silentium.engine.internal.cooldown.CooldownServiceImpl;
import net.silentium.engine.internal.user.UserDAOImpl;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

public final class SilentiumEngine extends JavaPlugin {

    @Getter
    private static SilentiumEngine instance;

    @Getter private LuckPerms luckPermsApi;
    @Getter private ProtocolManager protocolManager;
    @Getter private ColorfulGUI colorfulGUI;

    @Getter private EngineFileManager engineFileManager;
    @Getter private MenuManager menuManager;
    @Getter private LanguageManager languageManager;
    @Getter private ItemManager itemManager;
    @Getter private ItemLevelingManager itemLevelingManager;

    @Getter private Database database;
    @Getter private UserDAO userDAO;
    @Getter private CooldownService cooldownService;

    @Override
    public void onEnable() {
        instance = this;
        colorfulGUI = new ColorfulGUI(this);

        saveDefaultConfig();
        setupDependencies();
        if (!isEnabled()) return;

        setupFileManager();
        setupDatabase();
        if (!isEnabled()) return;

        setupServices();
        setupCommands();
        setupListeners();
        setupPacketListeners();

        getLogger().info("SilentiumEngine v" + getDescription().getVersion() + " has been enabled successfully.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling SilentiumEngine...");
        if (protocolManager != null) {
            protocolManager.removePacketListeners(this);
        }
        if (database != null) {
            database.close();
            getLogger().info("Database connection pool has been closed.");
        }
        getLogger().info("SilentiumEngine has been disabled.");
    }

    private void setupDependencies() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            this.luckPermsApi = provider.getProvider();
        } else {
            getLogger().severe("LuckPerms not found! Disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    private void setupFileManager() {
        this.engineFileManager = new EngineFileManager(this);
        this.engineFileManager.initialize();
    }

    private void setupDatabase() {
        try {
            FileConfiguration config = getConfig();
            String host = config.getString("database.host", "localhost");
            int port = config.getInt("database.port", 3306);
            String user = config.getString("database.user", "root");
            String password = config.getString("database.password", "password");
            String dbName = config.getString("database.database", "silentium_db");

            this.database = new Database(host, port, user, password, dbName);

            this.userDAO = new UserDAOImpl(this.database);
            CooldownDAO cooldownDAO = new CooldownDAOImpl(this.database);

            this.userDAO.createTable();
            cooldownDAO.createTable();

            this.cooldownService = new CooldownServiceImpl(cooldownDAO);

            getLogger().info("Database and services initialized successfully.");

        } catch (Exception e) {
            getLogger().severe("Failed to connect to the database. Reason: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void setupServices() {
        this.languageManager = new LanguageManager(this);
        this.menuManager = new MenuManager(this);
        this.itemManager = new ItemManager(this);
        this.itemLevelingManager = new ItemLevelingManager(this);
    }

    private void setupCommands() {
        Objects.requireNonNull(getCommand("language")).setExecutor(new LanguageCommand(this));
        Objects.requireNonNull(getCommand("giveitem")).setExecutor(new GiveItemCommand(this));
        Objects.requireNonNull(getCommand("sengine")).setExecutor(new SEngineCommand(this));
        Objects.requireNonNull(getCommand("sengine")).setExecutor(new SEngineCommand(this));
    }

    private void setupListeners() {
        getServer().getPluginManager().registerEvents(new CooldownListener((CooldownServiceImpl) this.cooldownService), this);
        getServer().getPluginManager().registerEvents(new ItemXPListener(this.itemLevelingManager), this);
        getServer().getPluginManager().registerEvents(new ScoreboardListener(), SilentiumEngine.getInstance());

        getLogger().info("Event listeners registered.");
    }

    private void setupPacketListeners() {
        if (this.protocolManager == null) {
            getLogger().severe("[DEBUG] ProtocolManager is NULL. Cannot register packet listeners.");
            return;
        }
        try {
            getLogger().info("[DEBUG] Attempting to register LanguagePacketListener...");
            protocolManager.addPacketListener(new LanguagePacketListener(this));
            getLogger().info("[DEBUG] LanguagePacketListener registration call has been made successfully.");
        } catch (Exception e) {
            getLogger().severe("[DEBUG] An exception occurred while registering packet listeners!");
            e.printStackTrace();
        }
    }

    @Getter
    public class EngineFileManager extends FileManager {

        private List<YAML> languageFiles;
        private List<YAML> menuFiles;
        private YAML config;

        public EngineFileManager(JavaPlugin plugin) {
            super(plugin);
        }

        public void initialize() {
            setupFilesAndFolders();
            initializeFilesAndFolders();

            this.languageFiles = loadYamls("lang");
            this.menuFiles = loadYamls("menus");
        }

        @Override
        protected void setupFilesAndFolders() {
            this.folders.add("lang");
            this.folders.add("menus");
            this.folders.add("items");

            this.config = new YAML(getDataFolder(), "config.yml");
            this.yamls.add(config);
        }
    }
}