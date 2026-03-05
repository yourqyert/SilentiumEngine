package net.silentium.engine.core.item;

import net.silentium.engine.SilentiumEngine;
import net.silentium.engine.api.config.YAML;
import net.silentium.engine.api.item.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemManager {

    private final SilentiumEngine plugin;
    private final Map<String, CustomItem> customItemTemplates = new HashMap<>();
    private final ItemFactory itemFactory;

    public ItemManager(SilentiumEngine plugin) {
        this.plugin = plugin;
        this.itemFactory = new ItemFactory(plugin);
        loadItems();
    }

    public void loadItems() {
        customItemTemplates.clear();
        List<YAML> itemFiles = plugin.getEngineFileManager().loadYamls("items");

        for (YAML yaml : itemFiles) {
            CustomItem itemTemplate = ItemParser.parse(yaml);
            if (itemTemplate != null && itemTemplate.getId() != null) {
                customItemTemplates.put(itemTemplate.getId().toLowerCase(), itemTemplate);
            }
        }
        plugin.getLogger().info("Loaded " + customItemTemplates.size() + " custom item templates.");
    }

    public ItemStack getItemStack(String id, Player player) {
        CustomItem template = customItemTemplates.get(id.toLowerCase());
        if (template == null) {
            return null;
        }
        return itemFactory.createItemStack(template, player, 1, 0);
    }

    public CustomItem getTemplate(String id) {
        return customItemTemplates.get(id.toLowerCase());
    }

    public ItemFactory getFactory() {
        return itemFactory;
    }
}