package com.leonardo.premiumcore.libs;

import com.leonardo.premiumcore.PremiumCore;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

@Singleton
public class Settings {

    @Getter
    private final HashMap<String, JsonObject> jsons = Maps.newHashMap();
    @Inject
    private PremiumCore core;

    public void createConfigurationFile(Plugin plugin, String resourcePath, String path, String name) {
        if (plugin == null) plugin = core;
        final String target = path + name;
        final String absolutePath = plugin.getDataFolder() + target;
        final File ctx = new File(plugin.getDataFolder() + path);
        if (!ctx.exists()) ctx.mkdirs();
        final File file = new File(absolutePath + ".json");
        try {
            if (!file.exists())
                Files.copy(plugin.getClass().getResourceAsStream(resourcePath + name + ".json"), file.toPath());
            final JsonObject jsonObject = new JsonParser().parse(new FileReader(file.getAbsolutePath())).getAsJsonObject();
            jsons.put(name, jsonObject);
        } catch (IOException ignored) {
        }
    }

    public JsonObject getObject(String name) {
        return jsons.get(name);
    }

    public void updateConfigurationFile(Plugin plugin, String path, String name) {
        if (plugin == null) plugin = core;
        final File file = new File(plugin.getDataFolder() + path + name + ".json");
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(getObject(name).getAsString());
            fileWriter.flush();
        } catch (IOException ignored) {
        }
    }



}
