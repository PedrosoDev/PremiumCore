package centralworks.premiumcore.guice;

import centralworks.premiumcore.PremiumCore;
import centralworks.premiumcore.libs.Settings;
import centralworks.premiumcore.libs.inventory.InventoryController;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.EntityManager;
import java.util.Properties;
import java.util.logging.Logger;

@EqualsAndHashCode(callSuper = false)
@Data(staticConstructor = "of")
public class PluginModule extends AbstractModule {

    private final PremiumCore premiumCore;
    private final Logger logger;

    protected void configure() {
        bind(PremiumCore.class).toInstance(premiumCore);
        bind(Logger.class).annotatedWith(Names.named("core")).toInstance(logger);
        bind(InventoryController.class);
    }

    @Provides
    @Singleton
    public Properties provideProperties() {
        final Settings.Navigate data = premiumCore.getData().navigate();
        final Properties settings = new Properties();
        settings.put(Environment.DRIVER, data.getString("Database.driver"));
        settings.put(Environment.URL, data.getString("Database.url"));
        settings.put(Environment.USER, data.getString("Database.user"));
        settings.put(Environment.PASS, data.getString("Database.password"));
        settings.put(Environment.DIALECT, data.getString("Database.dialect"));
        settings.put(Environment.SHOW_SQL, data.getString("Database.showSql"));
        settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, data.getString("Database.currentSessionContextClass"));
        settings.put(Environment.HBM2DDL_AUTO, data.getString("Database.hbm2ddlAuto"));
        settings.put(Environment.CONNECTION_PROVIDER, data.getString("Database.connectionProvider"));
        settings.put(Environment.USE_SECOND_LEVEL_CACHE, data.getString("Database.secondLevelCache"));
        settings.put(Environment.CACHE_REGION_FACTORY, data.getString("Database.cacheFactoryClass"));
        settings.put(Environment.CACHE_PROVIDER_CONFIG, data.getString("Database.cacheProviderClass"));
        settings.put(Environment.USE_QUERY_CACHE, data.getString("Database.cacheQueryCache"));
        settings.put(Environment.USE_STRUCTURED_CACHE, data.getString("Database.cacheStructureEntries"));
        settings.put("hibernate.javax.cache.provider", data.getString("Database.cacheProviderClass"));
        settings.put("net.sf.ehcache.configurationResourceName", "/META-INF/ehcache.xml");
        settings.put("hibernate.hikari.minimumIdle", data.getString("Database.minimumIdle"));
        settings.put("hibernate.hikari.maximumPoolSize", data.getString("Database.maximumPoolSize"));
        settings.put("hibernate.hikari.idleTimeout", data.getString("Database.idleTimeout"));
        return settings;
    }

    @Provides
    @Singleton
    public SessionFactory providesSessionFactory(Properties properties) {
        final Configuration configuration = new Configuration();
        configuration.setProperties(properties);
        premiumCore.getEntitiesClass().forEach(configuration::addAnnotatedClass);
        final ServiceRegistry serviceRegistry =
                new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    @Provides
    public Session providesSession(SessionFactory sessionFactory) {
        return sessionFactory.openSession();
    }

    @Provides
    public EntityManager providesEntityManager(SessionFactory sessionFactory) {
        return sessionFactory.createEntityManager();
    }

}
