package net.silentium.engine.core.scoreboard;

import net.silentium.engine.SilentiumEngine;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardListener implements Listener {

    private final Map<UUID, MainScoreboard> boards = new HashMap<>();

    public ScoreboardListener() {
        Bukkit.getServer().getScheduler().runTaskTimer(SilentiumEngine.getInstance(), () -> {
            for (MainScoreboard board : this.boards.values()) {
                board.update();
            }
        }, 0, 2400L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        MainScoreboard board = new MainScoreboard(player);

        boards.put(player.getUniqueId(), board);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        MainScoreboard board = this.boards.remove(player.getUniqueId());

        if (board != null) {
            board.getBoard().delete();
        }
    }

}
