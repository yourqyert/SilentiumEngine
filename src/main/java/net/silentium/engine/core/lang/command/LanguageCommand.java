package net.silentium.engine.core.lang.command;

import net.silentium.engine.SilentiumEngine;
import net.silentium.engine.core.lang.LanguageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LanguageCommand implements CommandExecutor {

    private final SilentiumEngine plugin;
    private final LanguageManager langManager;

    public LanguageCommand(SilentiumEngine plugin) {
        this.plugin = plugin;
        this.langManager = plugin.getLanguageManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            langManager.sendMessage(sender, "only-for-players");
            return true;
        }

        if (args.length == 0) {
            langManager.sendMessage(player, "lang.usage");
            return true;
        }

        String langCode = args[0].toLowerCase();
        plugin.getLanguageManager().setPlayerLanguage(player, langCode);

        return true;
    }
}