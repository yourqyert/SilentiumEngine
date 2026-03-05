package net.silentium.engine.core.item;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class ItemXPListener implements Listener {

    private final ItemLevelingManager levelingManager;

    public ItemXPListener(ItemLevelingManager levelingManager) {
        this.levelingManager = levelingManager;
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        ItemStack itemInHand = killer.getInventory().getItemInMainHand();
        levelingManager.addXp(killer, itemInHand, 10);
    }
}