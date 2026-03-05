package net.silentium.engine.core.menus;

import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.silentium.engine.SilentiumEngine;
import net.silentium.engine.api.config.YAML;
import net.silentium.engine.api.utils.text.ColorUtil;
import net.silentium.engine.core.menus.objects.MenuItem;
import net.silentium.engine.core.menus.objects.Requirement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class MenuParser {

    public static MenuConfig parse(YAML yaml) {
        MenuConfig menuConfig = new MenuConfig();
        menuConfig.setName(yaml.getName().replace(".yml", ""));

        menuConfig.setTitle(ColorUtil.colorize(yaml.getYaml().getString("title", "Menu")));
        menuConfig.setType(MenuConfig.MenuType.valueOf(yaml.getYaml().getString("type", "SIMPLE").toUpperCase()));
        menuConfig.setCommands(yaml.getYaml().getStringList("commands"));
        menuConfig.setRows(yaml.getYaml().getStringList("rows"));
        menuConfig.setUpdateRate(yaml.getYaml().getInt("update", -1));

        try {
            String openSoundStr = yaml.getYaml().getString("open_sound");
            if (openSoundStr != null) menuConfig.setOpenSound(Sound.valueOf(openSoundStr.toUpperCase()));
            String closeSoundStr = yaml.getYaml().getString("close_sound");
            if (closeSoundStr != null) menuConfig.setCloseSound(Sound.valueOf(closeSoundStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            SilentiumEngine.getInstance().getLogger().warning("Invalid sound specified in menu: " + yaml.getName());
        }

        ConfigurationSection itemsSection = yaml.getYaml().getConfigurationSection("items");
        if (itemsSection == null) return menuConfig;

        if (menuConfig.getType() == MenuConfig.MenuType.SIMPLE) {
            menuConfig.getPages().put(1, parseItems(itemsSection));
        } else {
            for (String pageKey : itemsSection.getKeys(false)) {
                try {
                    int pageNumber = Integer.parseInt(pageKey);
                    ConfigurationSection pageItemsSection = itemsSection.getConfigurationSection(pageKey);
                    if (pageItemsSection != null)
                        menuConfig.getPages().put(pageNumber, parseItems(pageItemsSection));
                } catch (NumberFormatException e) {
                    SilentiumEngine.getInstance().getLogger().warning("Invalid page number '" + pageKey + "' in menu: " + yaml.getName());
                }
            }
        }
        return menuConfig;
    }

    private static Map<String, MenuItem> parseItems(ConfigurationSection section) {
        Map<String, MenuItem> items = new HashMap<>();
        for (String key : section.getKeys(false)) {
            ConfigurationSection itemSection = section.getConfigurationSection(key);
            if (itemSection == null) continue;

            MenuItem menuItem = new MenuItem();
            menuItem.setKey(key);
            menuItem.setMaterial(itemSection.getString("material", "STONE"));
            menuItem.setAmount(itemSection.getInt("amount", 1));
            menuItem.setName(ColorUtil.colorize(itemSection.getString("name", "")));
            menuItem.setLore(ColorUtil.colorize(itemSection.getStringList("lore")));
            menuItem.setActions(itemSection.getStringList("action"));
            menuItem.setMask(itemSection.getString("mask"));
            menuItem.setCustomModelData(itemSection.getInt("customModelData", 0));
            menuItem.setItemStack(createItemStack(menuItem));

            Map<String, Requirement> requirements = new HashMap<>();
            ConfigurationSection reqSection = itemSection.getConfigurationSection("requirements");
            if (reqSection != null) {
                for (String reqKey : reqSection.getKeys(false)) {
                    ConfigurationSection req = reqSection.getConfigurationSection(reqKey);
                    if (req != null) {
                        try {
                            Requirement.RequirementType type = Requirement.RequirementType.valueOf(req.getString("type").toUpperCase());
                            Object value = req.get("value");
                            requirements.put(reqKey, new Requirement(type, value));
                        } catch (Exception e) {
                            SilentiumEngine.getInstance().getLogger().warning("Invalid requirement type in menu item: " + key);
                        }
                    }
                }
            }
            menuItem.setRequirements(requirements);
            items.put(key, menuItem);
        }
        return items;
    }

    private static ItemStack createItemStack(MenuItem menuItem) {
        ItemStack itemStack;
        String materialStr = menuItem.getMaterial();

        if (materialStr.toLowerCase().startsWith("hdb-")) {
            if (Bukkit.getPluginManager().isPluginEnabled("HeadDatabase")) {
                itemStack = new HeadDatabaseAPI().getItemHead(materialStr.substring(4));
                if (itemStack == null) {
                    SilentiumEngine.getInstance().getLogger().warning("HeadDatabase ID not found. Using STONE fallback.");
                    itemStack = new ItemStack(Material.STONE);
                }
            } else {
                SilentiumEngine.getInstance().getLogger().warning("HeadDatabase not found. Using STONE fallback.");
                itemStack = new ItemStack(Material.STONE);
            }
        } else {
            try {
                Material material = Material.matchMaterial(materialStr);
                itemStack = new ItemStack(material != null ? material : Material.STONE);
            } catch (Exception e) {
                SilentiumEngine.getInstance().getLogger().warning("Invalid material '" + materialStr + "'. Using STONE fallback.");
                itemStack = new ItemStack(Material.STONE);
            }
        }

        itemStack.setAmount(menuItem.getAmount());
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(menuItem.getName());
            meta.setLore(menuItem.getLore());
            if (menuItem.getCustomModelData() > 0) {
                meta.setCustomModelData(menuItem.getCustomModelData());
            }
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }
}