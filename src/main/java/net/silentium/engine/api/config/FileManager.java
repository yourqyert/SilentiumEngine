package net.silentium.engine.api.config;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class FileManager {

    @Getter protected List<YAML> yamls = new ArrayList<>();
    @Getter protected List<String> folders = new ArrayList<>();
    protected final JavaPlugin plugin;

    public FileManager(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null");
    }

    protected abstract void setupFilesAndFolders();

    public void initializeFilesAndFolders() {
        folders.forEach(this::createFolder);
        yamls.forEach(yaml -> yaml.initialize(plugin));
    }

    public void createFolder(String folder) {
        File file = new File(this.plugin.getDataFolder(), folder);
        if (!file.exists()) {
            file.mkdirs();
            this.plugin.getLogger().info("Created folder: " + file.getAbsolutePath());
        }
    }

    public List<YAML> loadYamls(String subfolderName) {
        File subfolder = new File(this.plugin.getDataFolder(), subfolderName);

        if (!subfolder.exists()) {
            subfolder.mkdirs();
            this.plugin.getLogger().info("Created subfolder: " + subfolder.getAbsolutePath());
            return new ArrayList<>();
        }

        if (!subfolder.isDirectory()) {
            this.plugin.getLogger().warning("Path is not a directory: " + subfolder.getAbsolutePath());
            return new ArrayList<>();
        }

        File[] files = subfolder.listFiles();
        if (files == null) {
            this.plugin.getLogger().warning("Could not list files in directory: " + subfolder.getAbsolutePath());
            return new ArrayList<>();
        }

        List<YAML> loadedYamls = new ArrayList<>();
        for (File file : files) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(".yml")) {
                try {
                    YAML yaml = new YAML(subfolder, file.getName());
                    yaml.initialize(this.plugin);

                    loadedYamls.add(yaml);
                    this.plugin.getLogger().info("Loaded YAML file: " + file.getName() + " from subfolder: " + subfolderName);
                } catch (Exception e) {
                    this.plugin.getLogger().severe("Failed to load YAML file: " + file.getAbsolutePath() + " - " + e.getMessage());
                }
            }
        }
        return loadedYamls;
    }

    public List<String> getYamls(String subfolderName) {
        File subfolder = new File(this.plugin.getDataFolder(), subfolderName);

        if (!subfolder.exists() || !subfolder.isDirectory()) {
            return new ArrayList<>();
        }

        File[] files = subfolder.listFiles();
        if (files == null) {
            return new ArrayList<>();
        }

        return java.util.Arrays.stream(files)
                .filter(file -> file.isFile() && file.getName().toLowerCase().endsWith(".yml"))
                .map(File::getName)
                .collect(Collectors.toList());
    }
}