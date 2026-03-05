package net.silentium.engine.core.cooldown;

import net.silentium.engine.internal.cooldown.CooldownServiceImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CooldownListener implements Listener {

    private final CooldownServiceImpl cooldownService;

    public CooldownListener(CooldownServiceImpl cooldownService) {
        this.cooldownService = cooldownService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        cooldownService.loadUserCache(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cooldownService.clearUserCache(event.getPlayer().getUniqueId());
    }
}
