package net.silentium.engine.core.lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.silentium.engine.SilentiumEngine;
import net.silentium.engine.api.config.YAML;
import net.silentium.engine.api.user.User;
import net.silentium.engine.api.utils.text.ColorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LanguageManager implements Listener {

    private final SilentiumEngine plugin;
    private final Map<String, FileConfiguration> languages = new HashMap<>();
    private final Map<UUID, String> playerLanguageCache = new ConcurrentHashMap<>();
    private final Pattern placeholderPattern = Pattern.compile("%engine_lang_([a-zA-Z0-9_.-]+)%");
    private String defaultLang;

    public LanguageManager(SilentiumEngine plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        loadLanguages();
    }

    public void loadLanguages() {
        languages.clear();
        List<YAML> langFiles = plugin.getEngineFileManager().loadYamls("lang");
        for (YAML yaml : langFiles) {
            String langCode = yaml.getName().replace(".yml", "");
            languages.put(langCode, yaml.getYaml());
        }
        this.defaultLang = plugin.getConfig().getString("default-language", "ru");
        plugin.getLogger().info("Loaded " + languages.size() + " languages. Default language is '" + defaultLang + "'.");
    }

    public String getPhrase(UUID playerId, String key, String... replacements) {
        String langCode = playerLanguageCache.getOrDefault(playerId, defaultLang);
        FileConfiguration langFile = languages.getOrDefault(langCode, languages.get(defaultLang));

        if (langFile == null) return "§cЛокализованное сообщение под ключом " + key + " не найден.";

        String phrase = langFile.getString(key);
        if (phrase == null) return null;

        phrase = phrase.replace("%prefix%", langFile.getString("prefix", ""));

        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                phrase = phrase.replace(replacements[i], replacements[i + 1]);
            }
        }

        return ColorUtil.colorize(phrase);
    }

    public List<String> getPhraseList(UUID playerId, String key, String... replacements) {
        String langCode = playerLanguageCache.getOrDefault(playerId, defaultLang);
        FileConfiguration langFile = languages.getOrDefault(langCode, languages.get(defaultLang));

        if (langFile == null) return Collections.singletonList("§cЛокализованное сообщение под ключом " + key + " не найден.");

        List<String> phraseList = langFile.getStringList(key);
        if (phraseList.isEmpty()) return Collections.emptyList();

        return phraseList.stream()
                .map(line -> {
                    line = line.replace("%prefix%", langFile.getString("prefix", ""));
                    for (int i = 0; i < replacements.length; i += 2) {
                        if (i + 1 < replacements.length) {
                            line = line.replace(replacements[i], replacements[i+1]);
                        }
                    }
                    return line;
                })
                .map(ColorUtil::colorize)
                .collect(Collectors.toList());
    }

    public void sendMessage(CommandSender sender, String key, String... replacements) {
        UUID id = (sender instanceof Player) ? ((Player) sender).getUniqueId() : null;

        List<String> messages = getPhraseList(id, key, replacements);

        if (messages.isEmpty()) {
            String singleMessage = getPhrase(id, key, replacements);
            if (singleMessage != null) {
                messages = Collections.singletonList(singleMessage);
            } else {
                sender.sendMessage("§cЛокализованное сообщение под ключом " + key + " не найден.");
                return;
            }
        }

        for (String message : messages) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
        }
    }

    public Component translateComponent(Component component, Player player) {
        if (component == null) return null;

        TextReplacementConfig config = TextReplacementConfig.builder()
                .match(placeholderPattern)
                .replacement((matchResult, builder) -> {
                    String key = matchResult.group(1);
                    String phrase = getPhrase(player.getUniqueId(), key);
                    if (phrase == null) return Component.text(matchResult.group(0));
                    return LegacyComponentSerializer.legacyAmpersand().deserialize(phrase);
                })
                .build();

        return component.replaceText(config);
    }

    public void setPlayerLanguage(Player player, String langCode) {
        if (!languages.containsKey(langCode)) {
            sendMessage(player, "lang.not-found", "%lang%", langCode);
            return;
        }

        playerLanguageCache.put(player.getUniqueId(), langCode);

        plugin.getUserDAO().findUserByUUID(player.getUniqueId()).thenAcceptAsync(userOptional -> {
            User user = userOptional.orElse(new User(player.getUniqueId(), player.getName()));
            user.setLanguage(langCode);
            plugin.getUserDAO().updateUser(user);

            String langName = languages.get(langCode).getString("lang-name", langCode);
            sendMessage(player, "lang.changed", "%lang%", langName.toUpperCase());
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        plugin.getUserDAO().findUserByUUID(playerUUID).thenAcceptAsync(userOptional -> {
            userOptional.ifPresentOrElse(
                    user -> {
                        user.setNickname(player.getName());
                        user.setLastSeen(new Timestamp(System.currentTimeMillis()));
                        plugin.getUserDAO().updateUser(user);

                        if (user.getLanguage() != null) {
                            playerLanguageCache.put(playerUUID, user.getLanguage());
                        } else {
                            playerLanguageCache.put(playerUUID, defaultLang);
                        }
                    },
                    () -> {
                        User newUser = new User(playerUUID, player.getName());
                        newUser.setLastSeen(new Timestamp(System.currentTimeMillis()));

                        String clientLocale = player.getLocale().split("_")[0].toLowerCase();
                        if (languages.containsKey(clientLocale)) {
                            newUser.setLanguage(clientLocale);
                            playerLanguageCache.put(playerUUID, clientLocale);
                        } else {
                            newUser.setLanguage(defaultLang);
                            playerLanguageCache.put(playerUUID, defaultLang);
                        }

                        plugin.getUserDAO().createUser(newUser);
                    }
            );
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerLanguageCache.remove(event.getPlayer().getUniqueId());

        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        plugin.getUserDAO().findUserByUUID(playerUUID).thenAcceptAsync(userOptional -> {
            userOptional.ifPresent(
                    user -> {
                        user.setNickname(player.getName());
                        user.setLastSeen(new Timestamp(System.currentTimeMillis()));
                        plugin.getUserDAO().updateUser(user);
                    }
            );
        });
    }

}