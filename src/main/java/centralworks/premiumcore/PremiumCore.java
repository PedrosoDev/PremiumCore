package centralworks.premiumcore;

import centralworks.premiumcore.annotations.CreateSettingsFile;
import centralworks.premiumcore.guice.PluginModule;
import centralworks.premiumcore.libs.Settings;
import centralworks.premiumcore.libs.inventory.InventoryController;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class PremiumCore extends JavaPlugin {

    @Getter
    private Injector injector;
    @CreateSettingsFile
    @Getter
    private Settings data;
    @Inject
    private InventoryController controller;

    public static PremiumCore getInstance() {
        return getPlugin(PremiumCore.class);
    }

    @Override
    public void onEnable() {
        createConfigurations(PremiumCore.class, this);
        this.injector = Guice.createInjector(PluginModule.of(this, getLogger()));
        this.injector.injectMembers(this);
        registerListener(controller);
    }

    @Override
    public void onDisable() {
    }

    public <T> void createConfigurations(Class<T> clazz, T instance) {
        try {
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isPrivate(field.getModifiers()) && field.isAnnotationPresent(CreateSettingsFile.class)) {
                    field.setAccessible(true);
                    final String path = field.getAnnotation(CreateSettingsFile.class).path();
                    final Settings settings = new Settings(this);
                    settings.setName(field.getName().toLowerCase() + ".yml");
                    settings.setPath(path);
                    field.set(instance, settings);
                    settings.initWithPath();
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
