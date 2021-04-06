package com.leonardo.premiumcore;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.leonardo.premiumcore.annotations.CreateYamlSettingsFile;
import com.leonardo.premiumcore.annotations.Jpa;
import com.leonardo.premiumcore.guice.PluginModule;
import com.leonardo.premiumcore.libs.Settings;
import com.leonardo.premiumcore.libs.YamlSettings;
import com.leonardo.premiumcore.libs.bookviewer.BookViewer;
import com.leonardo.premiumcore.libs.inventory.InventoryController;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PremiumCore extends JavaPlugin {

    @Inject
    @Getter
    private Settings settings;
    @Inject
    private InventoryController controller;
    @Inject
    @Getter
    private BookViewer bookViewer;
    @Getter
    private final List<Class<?>> entitiesClass = Lists.newArrayList();
    @Getter
    private final HashMap<String, Map.Entry<List<String>, List<String>>> enableRules = Maps.newHashMap();
    @Getter
    private Injector injector;
    @Getter
    private Gson prettierGson;
    @Getter
    private Gson gson;

    @SneakyThrows
    @Override
    public void onEnable() {
        this.injector = Guice.createInjector(PluginModule.of(this));
        this.injector.injectMembers(this);
        final GsonBuilder gsonBuilder = new GsonBuilder();
        this.prettierGson = gsonBuilder.setPrettyPrinting().create();
        this.gson = gsonBuilder.create();
        this.settings.createConfigurationFile(this, "/", "/", "database");
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            final Class<? extends Plugin> pluginClass = plugin.getClass();
            if (pluginClass.isAnnotationPresent(Jpa.class))
                this.entitiesClass.addAll(Arrays.asList(pluginClass.getAnnotation(Jpa.class).entities()));
        }
        registerListener(controller);
    }

    @Override
    public void onDisable() {
    }

    public <T> void createConfigurations(Class<T> clazz, T instance) {
        try {
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isPrivate(field.getModifiers()) && field.isAnnotationPresent(CreateYamlSettingsFile.class)) {
                    field.setAccessible(true);
                    final String path = field.getAnnotation(CreateYamlSettingsFile.class).path();
                    final YamlSettings yamlSettings = new YamlSettings(this);
                    yamlSettings.setName(field.getName().toLowerCase() + ".yml");
                    yamlSettings.setPath(path);
                    field.set(instance, yamlSettings);
                    yamlSettings.initWithPath();
                    field.setAccessible(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }
}
