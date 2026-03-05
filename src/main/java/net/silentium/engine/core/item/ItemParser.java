package net.silentium.engine.core.item;

import net.silentium.engine.api.config.YAML;
import net.silentium.engine.api.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;

import java.util.*;

public class ItemParser {

    public static CustomItem parse(YAML yaml) {
        CustomItem item = new CustomItem();
        ConfigurationSection config = yaml.getYaml();

        item.setId(config.getString("id"));
        item.setMaterial(Material.matchMaterial(config.getString("material", "STONE")));
        item.setBaseDisplayName(config.getString("display-name", ""));
        item.setBaseLore(config.getStringList("lore"));
        item.setBaseCustomModelData(config.getInt("custom-model-data", 0));
        item.setBaseEnchantments(parseEnchantments(config.getStringList("enchantments")));
        item.setBaseAttributes(parseAttributes(config.getStringList("attributes")));

        ConfigurationSection nbtSection = config.getConfigurationSection("nbt");
        if (nbtSection != null) {
            item.setNbtTags(nbtSection.getValues(true));
        }

        List<ItemFlag> flags = new ArrayList<>();
        for (String flagName : config.getStringList("item-flags")) {
            try {
                flags.add(ItemFlag.valueOf(flagName.toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }
        item.setItemFlags(flags);

        if (config.isConfigurationSection("leveling")) {
            CustomItem.LevelingConfig levelingConfig = new CustomItem.LevelingConfig();
            levelingConfig.setMaxLevel(config.getInt("leveling.max-level", 1));
            levelingConfig.setXpSource(config.getString("leveling.xp-source", ""));
            levelingConfig.setXpFormula(config.getString("leveling.xp-formula", ""));
            item.setLevelingConfig(levelingConfig);
        }

        if (config.isConfigurationSection("levels")) {
            Map<Integer, CustomItem.LevelData> levelDataMap = new HashMap<>();
            for (String key : config.getConfigurationSection("levels").getKeys(false)) {
                int level = Integer.parseInt(key);
                ConfigurationSection levelSection = config.getConfigurationSection("levels." + key);
                CustomItem.LevelData levelData = new CustomItem.LevelData();
                levelData.setDisplayName(levelSection.getString("display-name"));
                levelData.setLore(levelSection.getStringList("lore"));
                levelData.setCustomModelData(levelSection.getInt("custom-model-data"));
                levelData.setEnchantments(parseEnchantments(levelSection.getStringList("enchantments")));
                levelData.setAttributes(parseAttributes(levelSection.getStringList("attributes")));
                levelDataMap.put(level, levelData);
            }
            item.setLevelData(levelDataMap);
        }

        return item;
    }

    private static Map<Enchantment, Integer> parseEnchantments(List<String> list) {
        Map<Enchantment, Integer> enchants = new HashMap<>();
        if (list == null) return enchants;
        for (String s : list) {
            String[] parts = s.split(";");
            Enchantment enchant = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(parts[0].toLowerCase()));
            if (enchant != null) {
                enchants.put(enchant, Integer.parseInt(parts[1]));
            }
        }
        return enchants;
    }

    private static Map<Attribute, AttributeModifier> parseAttributes(List<String> list) {
        Map<Attribute, AttributeModifier> attributes = new HashMap<>();
        if (list == null) return attributes;
        for (String s : list) {
            String[] parts = s.split(";");
            Attribute attribute = Registry.ATTRIBUTE.get(NamespacedKey.minecraft(parts[0].toLowerCase()));
            if (attribute == null) continue;

            double amount = Double.parseDouble(parts[1]);
            AttributeModifier.Operation op = AttributeModifier.Operation.ADD_NUMBER;
            EquipmentSlot slot = parts.length > 2 ? EquipmentSlot.valueOf(parts[2].toUpperCase()) : null;

            NamespacedKey modifierKey = new NamespacedKey("silentium", "item_attribute_" + attribute.getKey().getKey() + "_" + UUID.randomUUID());
            AttributeModifier modifier = new AttributeModifier(modifierKey, amount, op, slot.getGroup());

            attributes.put(attribute, modifier);
        }
        return attributes;
    }
}