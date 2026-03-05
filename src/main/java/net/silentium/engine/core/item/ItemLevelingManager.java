package net.silentium.engine.core.item;

import de.tr7zw.nbtapi.NBTItem;
import net.silentium.engine.SilentiumEngine;
import net.silentium.engine.api.item.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ItemLevelingManager {

    private final SilentiumEngine plugin;
    private final ScriptEngine scriptEngine;

    public ItemLevelingManager(SilentiumEngine plugin) {
        this.plugin = plugin;
        this.scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
    }

    public void addXp(Player player, ItemStack item, int amount) {
        if (item == null || item.getAmount() == 0) return;

        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasTag("silentium_id")) return;

        String itemId = nbtItem.getString("silentium_id");
        CustomItem template = plugin.getItemManager().getTemplate(itemId);
        if (template == null || template.getLevelingConfig() == null) return;

        int currentLevel = nbtItem.getInteger("item_level");
        int currentXp = nbtItem.getInteger("item_xp");
        int maxLevel = template.getLevelingConfig().getMaxLevel();

        if (currentLevel >= maxLevel) return;

        int newXp = currentXp + amount;
        int xpForNextLevel = calculateXpForLevel(template, currentLevel);

        if (newXp >= xpForNextLevel) {
            levelUp(player, item, nbtItem, template);
        } else {
            nbtItem.setInteger("item_xp", newXp);
            nbtItem.applyNBT(item);
        }
    }

    private void levelUp(Player player, ItemStack item, NBTItem nbtItem, CustomItem template) {
        int currentLevel = nbtItem.getInteger("item_level");
        int newLevel = currentLevel + 1;

        ItemStack newItem = plugin.getItemManager().getFactory().createItemStack(template, player, newLevel, 0);

        item.setItemMeta(newItem.getItemMeta());
        item.setType(newItem.getType());

        player.sendMessage("Ваш предмет " + newItem.getItemMeta().getDisplayName() + " достиг " + newLevel + " уровня!");
    }

    private int calculateXpForLevel(CustomItem template, int level) {
        String formula = template.getLevelingConfig().getXpFormula().replace("%level%", String.valueOf(level));
        try {
            Object result = scriptEngine.eval(formula);
            if (result instanceof Number) {
                return ((Number) result).intValue();
            }
        } catch (ScriptException e) {
            plugin.getLogger().warning("Invalid XP formula for item " + template.getId() + ": " + e.getMessage());
        }
        return Integer.MAX_VALUE;
    }
}