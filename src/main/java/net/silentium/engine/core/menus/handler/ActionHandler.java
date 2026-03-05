package net.silentium.engine.core.menus.handler;

import me.clip.placeholderapi.PlaceholderAPI;
import net.silentium.engine.core.menus.MenuConfig;
import net.silentium.engine.core.menus.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ActionHandler {

    private final MenuManager menuManager;

    public ActionHandler(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    public void process(Player player, List<String> actions, MenuConfig menuConfig, int currentPage) {
        if (actions == null || actions.isEmpty()) return;

        actions.forEach(action -> {
            String processedAction = PlaceholderAPI.setPlaceholders(player, action);
            String[] parts = processedAction.split("]", 2);
            String type = parts[0].replace("[", "").toLowerCase();
            String value = parts.length > 1 ? parts[1].trim() : "";

            switch (type) {
                case "console":
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value);
                    break;
                case "player":
                    player.performCommand(value);
                    break;
                case "message":
                    player.sendMessage(value);
                    break;
                case "close":
                    player.closeInventory();
                    break;
                case "menu":
                    menuManager.openMenu(player, value, 1);
                    break;
                case "nextpage":
                    menuManager.openMenu(player, menuConfig.getName(), currentPage + 1);
                    break;
                case "previouspage":
                    menuManager.openMenu(player, menuConfig.getName(), currentPage - 1);
                    break;
            }
        });
    }
}