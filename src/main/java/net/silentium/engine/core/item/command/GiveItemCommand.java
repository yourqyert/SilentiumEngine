package net.silentium.engine.core.item.command;

import net.silentium.engine.SilentiumEngine;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GiveItemCommand implements CommandExecutor {

    private final SilentiumEngine plugin;

    public GiveItemCommand(SilentiumEngine plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("Usage: /giveitem <item_id>");
            return true;
        }

        Player player = (Player) sender;
        String itemId = args[0];

        ItemStack item = plugin.getItemManager().getItemStack(itemId, player);

        if (item == null) {
            player.sendMessage("§cItem with ID '" + itemId + "' not found.");
            return true;
        }

        player.getInventory().addItem(item);
        player.sendMessage("§aYou received item: " + itemId);
        return true;
    }
}