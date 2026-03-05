package net.silentium.engine.core.menus;

import me.xflyiwnl.colorfulgui.ColorfulGUI;
import net.silentium.engine.SilentiumEngine;
import net.silentium.engine.api.config.YAML;
import net.silentium.engine.core.lang.LanguageManager;
import net.silentium.engine.core.menus.handler.ActionHandler;
import net.silentium.engine.core.menus.provider.GenericPaginatedProvider;
import net.silentium.engine.core.menus.provider.GenericSimpleProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;
import java.util.*;

public class MenuManager implements Listener {

    private final SilentiumEngine plugin;
    private final ColorfulGUI colorfulGUI;
    private final LanguageManager langManager;
    private final Map<String, MenuConfig> menus = new HashMap<>();
    private final Map<UUID, String> openMenus = new HashMap<>();

    private final ActionHandler actionHandler = new ActionHandler(this);
    private final RequirementService requirementService = new RequirementService();

    public MenuManager(SilentiumEngine plugin) {
        this.plugin = plugin;
        this.langManager = plugin.getLanguageManager();
        this.colorfulGUI = plugin.getColorfulGUI();
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        loadMenus();
    }

    public void loadMenus() {
        menus.clear();
        List<YAML> menuFiles = plugin.getEngineFileManager().getMenuFiles();
        if (menuFiles == null || menuFiles.isEmpty()) {
            plugin.getLogger().info("No menus found to load.");
            return;
        }

        for (YAML yaml : menuFiles) {
            MenuConfig menuConfig = MenuParser.parse(yaml);
            menus.put(menuConfig.getName().toLowerCase(), menuConfig);
            registerMenuCommands(menuConfig);
        }
        plugin.getLogger().info("Loaded " + menus.size() + " menus.");
    }

    private void registerMenuCommands(MenuConfig menuConfig) {
        List<String> commands = menuConfig.getCommands();
        if (commands == null || commands.isEmpty()) {
            return;
        }

        CommandMap commandMap = getCommandMap();
        if (commandMap == null) {
            plugin.getLogger().severe("Could not access CommandMap. Dynamic menus commands will not work.");
            return;
        }

        String mainCommand = commands.getFirst();
        List<String> aliases = commands.subList(1, commands.size());

        net.silentium.engine.core.menus.command.MenuCommand menuCommand = new net.silentium.engine.core.menus.command.MenuCommand(mainCommand, aliases, this, menuConfig.getName());
        commandMap.register("SilentiumEngine", menuCommand);
    }

    private CommandMap getCommandMap() {
        try {
            Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            return (CommandMap) f.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void openMenu(Player player, String menuName, int page) {
        MenuConfig menuConfig = menus.get(menuName.toLowerCase());
        if (menuConfig == null) {
            langManager.sendMessage(player, "menu-not-found", "%menu%", menuName);
            return;
        }

        String title = menuConfig.getTitle().replace("%page%", String.valueOf(page));

        if (menuConfig.getType() == MenuConfig.MenuType.PAGINATED) {
            colorfulGUI.paginated()
                    .title(title)
                    .rows(menuConfig.getRows().size())
                    .mask(menuConfig.getRows())
                    .holder(new GenericPaginatedProvider(player, menuConfig, page, actionHandler, requirementService))
                    .build();
        } else {
            colorfulGUI.gui()
                    .title(title)
                    .rows(menuConfig.getRows().size())
                    .mask(menuConfig.getRows())
                    .holder(new GenericSimpleProvider(player, menuConfig, page, actionHandler, requirementService))
                    .build();
        }

        openMenus.put(player.getUniqueId(), menuName.toLowerCase());
        if (menuConfig.getOpenSound() != null) {
            player.playSound(player.getLocation(), menuConfig.getOpenSound(), 1f, 1f);
        }
    }

    public String getOpenMenuName(Player player) {
        return openMenus.get(player.getUniqueId());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        openMenus.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        openMenus.remove(event.getPlayer().getUniqueId());
    }
}