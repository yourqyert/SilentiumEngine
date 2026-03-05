package net.silentium.engine.core.menus.command;

import net.silentium.engine.SilentiumEngine;
import net.silentium.engine.core.lang.LanguageManager;
import net.silentium.engine.core.menus.MenuManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MenuCommand extends Command {

    private final MenuManager menuManager;
    private final LanguageManager langManager;
    private final String menuName;

    public MenuCommand(@NotNull String name, @NotNull List<String> aliases, MenuManager menuManager, String menuName) {
        super(name);
        setAliases(aliases);
        setPermission("silentium.menu." + menuName);

        this.menuManager = menuManager;
        this.menuName = menuName;
        this.langManager = SilentiumEngine.getInstance().getLanguageManager();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            langManager.sendMessage(sender, "only-for-players");
            return true;
        }

        if (getPermission() != null && !sender.hasPermission(getPermission())) {
            langManager.sendMessage(sender, "no-permission");
            return true;
        }

        menuManager.openMenu((Player) sender, menuName, 1);
        return true;
    }
}