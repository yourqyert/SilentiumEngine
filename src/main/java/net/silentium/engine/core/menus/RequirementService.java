package net.silentium.engine.core.menus;

import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import net.silentium.engine.core.menus.objects.Requirement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Map;

public class RequirementService {

    private Economy vaultEconomy = null;

    public RequirementService() {
        setupEconomy();
    }

    private void setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        vaultEconomy = rsp.getProvider();
    }

    public boolean checkAll(Player player, Map<String, Requirement> requirements) {
        if (requirements == null || requirements.isEmpty()) {
            return true;
        }
        for (Requirement req : requirements.values()) {
            if (!check(player, req)) {
                return false;
            }
        }
        return true;
    }

    private boolean check(Player player, Requirement requirement) {
        switch (requirement.getType()) {
            case PERMISSION:
                return player.hasPermission((String) requirement.getValue());

            case MONEY:
                if (vaultEconomy == null) return false;
                return vaultEconomy.has(player, ((Number) requirement.getValue()).doubleValue());

            case RUBLES:
                return true;

            case ITEM:
                String[] parts = ((String) requirement.getValue()).split(";", 2);
                Material material = Material.matchMaterial(parts[0].toUpperCase());
                int amount = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
                if (material == null) return false;
                return player.getInventory().contains(material, amount);

            case PLACEHOLDER:
                // TODO: реализовать более сложные сравнения (>, <, !=)
                String toCheck = (String) requirement.getValue();
                String[] placeholderParts = toCheck.split("==", 2);
                if (placeholderParts.length != 2) return false;
                String placeholder = placeholderParts[0].trim();
                String expectedValue = placeholderParts[1].trim();
                String actualValue = PlaceholderAPI.setPlaceholders(player, placeholder);
                return actualValue.equals(expectedValue);

            default:
                return true;
        }
    }
}