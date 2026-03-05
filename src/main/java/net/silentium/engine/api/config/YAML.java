package net.silentium.engine.api.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Getter
public class YAML {

    private final File file;
    private FileConfiguration yaml;
    private final String name;

    public YAML(File dataFolder, String name) {
        this.name = Objects.requireNonNull(name, "YAML name cannot be null");
        this.file = new File(dataFolder, name);
    }

    public void initialize(JavaPlugin plugin) {
        if (!file.exists()) {
            try {
                plugin.saveResource(name, false);
                plugin.getLogger().info("Copied default resource: " + name);
            } catch (IllegalArgumentException e) {
                try {
                    if (file.createNewFile()) {
                        plugin.getLogger().info("Created empty file: " + name);
                    }
                } catch (IOException ioException) {
                    throw new RuntimeException("Failed to create file: " + file.getAbsolutePath(), ioException);
                }
            }
        }

        reload();
    }

    public void save() {
        try {
            yaml.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save YAML file: " + file.getAbsolutePath(), e);
        }
    }

    public void reload() {
        this.yaml = YamlConfiguration.loadConfiguration(file);
    }
}
