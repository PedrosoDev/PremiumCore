package com.leonardo.premiumcore.libs;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
@Data
public class YamlSettings {

    @Getter
    private static final HashMap<String, YamlSettings> cache = Maps.newHashMap();
    private final Plugin plugin;
    private String name;
    @Setter(value = AccessLevel.PRIVATE)
    private YamlConfiguration configuration;
    private String path;
    @Setter(value = AccessLevel.PRIVATE)
    private String source;

    public YamlSettings(Plugin plugin) {
        this.plugin = plugin;
    }

    public YamlSettings initWithPath() {
        createOrCopyArchive(name, path);
        saveInCache();
        return this;
    }

    public YamlSettings initWithoutPath() {
        createOrCopyArchive(name, "/");
        saveInCache();
        return this;
    }

    public void moveTo(String path) {
        try {
            com.google.common.io.Files.move(getFile(), new File(path));
            setSource(new File(path).getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void renameTo(String name) {
        final List<String> list = Splitter.on("/").trimResults().omitEmptyStrings().splitToList(getSource());
        list.set(list.size() - 1, name);
        moveTo(Joiner.on("/").join(list));
    }

    public void updateConfiguration(String absolutePath) {
        this.configuration = YamlConfiguration.loadConfiguration(new File(absolutePath));
    }

    public void createOrCopyArchive(String name, String path) {
        final String target = path + name;
        final String absolutePath = plugin.getDataFolder() + target;
        final File dir = new File(plugin.getDataFolder() + path);
        if (!dir.exists()) dir.mkdirs();
        final File original = new File(absolutePath);
        if (!original.exists()) {
            try {
                Files.copy(plugin.getClass().getResourceAsStream(target), original.toPath());
            } catch (IOException e) {
                Bukkit.getLogger().warning("Ocorreu um erro ao carregar a configuração \"" + name + "\". Diretório informado: " + absolutePath + "");
            }
        }
        updateConfiguration(absolutePath);
        setSource(absolutePath);
    }

    public void saveInCache() {
        cache.put(name.toLowerCase(), this);
    }

    public void save() {
        if (getFile().exists()) {
            try {
                configuration.save(getFile());
            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage("§cOcorreu um erro no salvamento do arquivo: " + getFile().getName());
            }
        }
    }

    public Navigate navigate() {
        return new Navigate(this);
    }

    public File getFile() {
        return new File(getSource());
    }

    public static class Navigate {

        @Getter
        private final YamlSettings yamlSettings;
        @Getter
        private final YamlConfiguration config;

        public Navigate(YamlSettings yamlSettings) {
            this.yamlSettings = yamlSettings;
            config = yamlSettings.getConfiguration();
        }

        public String getString(String... road) {
            if (!has(road)) return "";
            return config.getString(Joiner.on(".").join(road));
        }

        public Integer getInt(String... road) {
            if (!has(road)) return 0;
            return config.getInt(Joiner.on(".").join(road));
        }

        public Double getDouble(String... road) {
            if (!has(road)) return 0d;
            return config.getDouble(Joiner.on(".").join(road));
        }

        public boolean getBoolean(String... road) {
            return config.getBoolean(Joiner.on(".").join(road));
        }

        public Object getResult(String... road) {
            return config.get(Joiner.on(".").join(road));
        }

        public Set<String> section(String path) {
            return config.getConfigurationSection(path).getKeys(false);
        }

        public List<String> getList(String... path) {
            return config.getStringList(Joiner.on(".").join(path));
        }

        public Long getLong(String... path) {
            if (!has(path)) return 0L;
            return config.getLong(Joiner.on(".").join(path));
        }

        public void saveLocation(Location location, String... paths) {
            final String path = "Locations." + Joiner.on(".").join(paths) + ".";
            set(path + "world", location.getWorld().getName());
            set(path + "x", location.getX());
            set(path + "y", location.getY());
            set(path + "z", location.getZ());
            set(path + "yaw", String.valueOf(location.getYaw()));
            set(path + "pitch", String.valueOf(location.getPitch()));
            yamlSettings.save();
        }

        public String getMessage(String... road) {
            if (!has("Messages." + Joiner.on(".").join(road))) return "";
            return config.getString("Messages." + Joiner.on(".").join(road)).replace("&", "§");
        }

        public String getColorfulString(String... road) {
            if (!has(road)) return "";
            return config.getString(Joiner.on(".").join(road)).replace("&", "§");
        }

        public List<String> getListMessage(String... road) {
            return getList("Messages." + Joiner.on(".").join(road)).stream().map(s -> s.replaceAll("&", "§")).collect(Collectors.toList());
        }

        public List<String> getColorfulList(String... road) {
            return getList(Joiner.on(".").join(road)).stream().map(s -> s.replaceAll("&", "§")).collect(Collectors.toList());
        }

        public Float getFloat(String... path) {
            return Float.valueOf(getResult(path).toString());
        }

        public Boolean has(String... path) {
            return getResult(path) != null;
        }

        public void set(String path, Object value) {
            config.set(path, value);
        }

    }


}
