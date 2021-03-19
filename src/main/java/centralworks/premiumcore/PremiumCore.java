package centralworks.premiumcore;

import centralworks.premiumcore.annotations.CreateSettingsFile;
import centralworks.premiumcore.guice.PluginModule;
import centralworks.premiumcore.libs.Settings;
import centralworks.premiumcore.libs.inventory.InventoryController;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class PremiumCore extends JavaPlugin {

    @Getter
    private Injector injector;
    @CreateSettingsFile
    @Getter
    private Settings data;
    @Inject
    private InventoryController controller;
    @Getter
    private final List<Class<?>> entitiesClass = Lists.newArrayList();

    public static PremiumCore getInstance() {
        return getPlugin(PremiumCore.class);
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        createConfigurations(PremiumCore.class, this);
        this.injector = Guice.createInjector(PluginModule.of(this, getLogger()));
        this.injector.injectMembers(this);
        registerListener(controller);
        Arrays.stream(Bukkit.getPluginManager().loadPlugins(new File(this.getDataFolder() + "/plugins/"))).forEach(plugin -> {
            entitiesClass.addAll(((CoreConfig) plugin).getEntitiesClass());
            Bukkit.getPluginManager().enablePlugin(plugin);
        });
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

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}
