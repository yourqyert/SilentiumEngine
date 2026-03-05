package net.silentium.engine.api.item;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CustomItem {

    private String id;
    private Material material;
    private String baseDisplayName;
    private List<String> baseLore;
    private int baseCustomModelData;
    private Map<Enchantment, Integer> baseEnchantments;
    private Map<Attribute, AttributeModifier> baseAttributes;
    private List<ItemFlag> itemFlags;
    private Map<String, Object> nbtTags;

    private LevelingConfig levelingConfig;
    private Map<Integer, LevelData> levelData;

    @Getter
    @Setter
    public static class LevelingConfig {
        private int maxLevel;
        private String xpSource;
        private String xpFormula;
    }

    @Getter
    @Setter
    public static class LevelData {
        private String displayName;
        private List<String> lore;
        private int customModelData;
        private Map<Enchantment, Integer> enchantments;
        private Map<Attribute, AttributeModifier> attributes;
    }
}