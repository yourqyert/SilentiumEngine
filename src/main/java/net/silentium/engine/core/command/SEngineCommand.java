package net.silentium.engine.core.command;

import net.silentium.engine.SilentiumEngine;
import net.silentium.engine.core.lang.LanguageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SEngineCommand implements CommandExecutor, TabCompleter {

    private final SilentiumEngine plugin;
    private final LanguageManager langManager;

    public SEngineCommand(SilentiumEngine plugin) {
        this.plugin = plugin;
        this.langManager = plugin.getLanguageManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            langManager.sendMessage(sender, "sengine.system", "%version%", plugin.getDescription().getVersion());
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("silentium.admin.reload")) {
                langManager.sendMessage(sender, "no-permission");
                return true;
            }

            long startTime = System.currentTimeMillis();

            plugin.reloadConfig();

            plugin.getLanguageManager().loadLanguages();
            plugin.getMenuManager().loadMenus();
            plugin.getItemManager().loadItems();

            long duration = System.currentTimeMillis() - startTime;
            langManager.sendMessage(sender, "sengine.reloaded", "%duration%", String.valueOf(duration));
            return true;
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            if (sender.hasPermission("silentium.admin.reload")) {
                return Collections.singletonList("reload");
            }
        }
        return Collections.emptyList();
    }
}