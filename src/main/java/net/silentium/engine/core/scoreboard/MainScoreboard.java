package net.silentium.engine.core.scoreboard;

import lombok.Getter;
import net.silentium.engine.SilentiumEngine;
import net.silentium.engine.api.scoreboard.Board;
import net.silentium.engine.core.lang.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
public class MainScoreboard {

    private final SilentiumEngine plugin = SilentiumEngine.getInstance();
    private final LanguageManager langManager = plugin.getLanguageManager();

    private Player player;
    private Board board;

    public MainScoreboard(Player player) {
        if (player != null) {
            this.player = player;
            this.board = new Board(player);

            showLoadingState();
        }
    }

    public void showLoadingState() {
        board.updateTitle(langManager.getPhrase(player.getUniqueId(), "scoreboard.loading.title"));
        board.updateLines(langManager.getPhraseList(player.getUniqueId(), "scoreboard.loading.lines"));
    }

    public void update() {
        plugin.getUserDAO().findUserByUUID(player.getUniqueId()).thenAccept(userOptional -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                userOptional.ifPresent(user -> {
                    board.updateTitle(langManager.getPhrase(player.getUniqueId(), "scoreboard.main.title"));
                    board.updateLines(langManager.getPhraseList(player.getUniqueId(), "scoreboard.main.lines",
                            "%nickname%", player.getName(),
                            "%roubles%", String.valueOf(user.getRoubles()),
                            "%kills_mutant%", String.valueOf(user.getMutantKills()),
                            "%kills_player%", String.valueOf(user.getPlayerKills()),
                            "%kills_boss%", String.valueOf(user.getBossKills()),
                            "%deaths%", String.valueOf(user.getDeaths())
                    ));
                });
            });
        });
    }
}