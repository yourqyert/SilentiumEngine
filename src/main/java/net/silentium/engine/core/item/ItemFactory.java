package net.silentium.engine.core.item;

import de.tr7zw.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.silentium.engine.SilentiumEngine;
import net.silentium.engine.api.item.CustomItem;
import net.silentium.engine.core.lang.LanguageManager;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemFactory {

    private final SilentiumEngine plugin;

    public ItemFactory(SilentiumEngine plugin) {
        this.plugin = plugin;
    }

    public ItemStack createItemStack(CustomItem template, Player player, int level, int xp) {
        ItemStack item = new ItemStack(template.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        CustomItem.LevelData levelData = template.getLevelData() != null ? template.getLevelData().get(level) : null;

        String nameTemplate = (levelData != null && levelData.getDisplayName() != null) ? levelData.getDisplayName() : template.getBaseDisplayName();
        List<String> loreTemplate = (levelData != null && levelData.getLore() != null) ? levelData.getLore() : template.getBaseLore();
        int modelData = (levelData != null && levelData.getCustomModelData() > 0) ? levelData.getCustomModelData() : template.getBaseCustomModelData();
        Map<Enchantment, Integer> enchants = (levelData != null && levelData.getEnchantments() != null) ? levelData.getEnchantments() : template.getBaseEnchantments();
        Map<Attribute, AttributeModifier> attributes = (levelData != null && levelData.getAttributes() != null) ? levelData.getAttributes() : template.getBaseAttributes();

        LanguageManager langManager = plugin.getLanguageManager();
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();

        String finalNameTemplate = nameTemplate.replace("%level%", String.valueOf(level));
        Component nameComponent = serializer.deserialize(finalNameTemplate);
        Component translatedName = langManager.translateComponent(nameComponent, player);
        meta.displayName(translatedName);

        if (loreTemplate != null) {
            List<Component> translatedLore = loreTemplate.stream()
                    .map(line -> line.replace("%level%", String.valueOf(level)))
                    .map(serializer::deserialize)
                    .map(component -> langManager.translateComponent(component, player))
                    .collect(Collectors.toList());
            meta.lore(translatedLore);
        }

        if (modelData > 0) meta.setCustomModelData(modelData);
        if (enchants != null) enchants.forEach((enchant, lvl) -> meta.addEnchant(enchant, lvl, true));
        if (attributes != null) attributes.forEach(meta::addAttributeModifier);
        if (template.getItemFlags() != null) meta.addItemFlags(template.getItemFlags().toArray(new ItemFlag[0]));

        item.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("silentium_id", template.getId());
        if (template.getLevelingConfig() != null) {
            nbtItem.setInteger("item_level", level);
            nbtItem.setInteger("item_xp", xp);
        }
        if (template.getNbtTags() != null) {
            template.getNbtTags().forEach(nbtItem::setObject);
        }

        return nbtItem.getItem();
    }
}