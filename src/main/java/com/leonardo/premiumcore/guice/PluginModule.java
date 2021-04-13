package com.leonardo.premiumcore.guice;

import com.google.gson.JsonObject;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.leonardo.premiumcore.PremiumCore;
import com.leonardo.premiumcore.libs.ReflectionUtils;
import com.leonardo.premiumcore.libs.Settings;
import com.leonardo.premiumcore.libs.bookviewer.BookViewer;
import com.leonardo.premiumcore.libs.bookviewer.BookViewerCurrently;
import com.leonardo.premiumcore.libs.inventory.InventoryController;
import lombok.AllArgsConstructor;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.lang.reflect.Method;
import java.util.Properties;
import java.util.function.Function;

@AllArgsConstructor(staticName = "of")
public class PluginModule extends AbstractModule {

    private final PremiumCore core;

    protected void configure() {
        bind(PremiumCore.class).toInstance(core);
        bind(Settings.class);
        bind(InventoryController.class);
        bind(BookViewer.class).to(BookViewerCurrently.class);
        bind(PluginManager.class).toInstance(core.getServer().getPluginManager());
        bind(PluginLoader.class).toInstance(core.getPluginLoader());
    }

    @Provides
    @Singleton
    @Named("player#getHandle")
    public Method providePlayerGetHandle() throws NoSuchMethodException, ClassNotFoundException {
        return ReflectionUtils.getMethod("CraftPlayer", ReflectionUtils.PackageType.CRAFTBUKKIT_ENTITY, "getHandle");
    }

    public void createConfigurations(Settings config) {
        config.createConfigurationFile(core, "/", "/", "database");
    }

    @Provides
    @Singleton
    @Inject
    public Properties provideProperties(Settings config) {
        createConfigurations(config);
        final JsonObject data = config.getObject(core, "database");
        final Function<String, String> fun = (param) -> data.get(param).getAsString();
        final Properties settings = new Properties();
        settings.put(Environment.DRIVER, fun.apply("driver"));
        settings.put(Environment.URL, fun.apply("url"));
        settings.put(Environment.USER, fun.apply("user"));
        settings.put(Environment.PASS, fun.apply("password"));
        settings.put(Environment.DIALECT, fun.apply("dialect"));
        settings.put(Environment.SHOW_SQL, fun.apply("showSql"));
        settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, fun.apply("currentSessionContextClass"));
        settings.put(Environment.HBM2DDL_AUTO, fun.apply("hbm2ddlAuto"));
        settings.put(Environment.CONNECTION_PROVIDER, fun.apply("connectionProvider"));
        settings.put(Environment.USE_SECOND_LEVEL_CACHE, fun.apply("secondLevelCache"));
        settings.put(Environment.CACHE_REGION_FACTORY, fun.apply("cacheFactoryClass"));
        settings.put(Environment.CACHE_PROVIDER_CONFIG, fun.apply("cacheProviderClass"));
        settings.put(Environment.USE_QUERY_CACHE, fun.apply("cacheQueryCache"));
        settings.put(Environment.USE_STRUCTURED_CACHE, fun.apply("cacheStructureEntries"));
        settings.put("hibernate.hikari.minimumIdle", fun.apply("minimumIdle"));
        settings.put("hibernate.hikari.maximumPoolSize", fun.apply("maximumPoolSize"));
        settings.put("hibernate.hikari.idleTimeout", fun.apply("idleTimeout"));
        settings.put("hibernate.hikari.connectionTimeout", fun.apply("connectionTimeout"));
        settings.put("hibernate.hikari.autoCommit", fun.apply("autoCommit"));
        settings.put("net.sf.ehcache.configurationResourceName", "/META-INF/ehcache.xml");
        return settings;
    }

    @Provides
    @Singleton
    public SessionFactory providesSessionFactory(Properties properties) {
        final Configuration configuration = new Configuration();
        configuration.setProperties(properties);
        core.getEntitiesClass().forEach(configuration::addAnnotatedClass);
        final ServiceRegistry serviceRegistry =
                new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

}
