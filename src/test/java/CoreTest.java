import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.junit.Test;

import java.util.Properties;
import java.util.function.Function;

public class CoreTest {

    private Injector injector;
    @Inject
    private SessionFactory sessionFactory;

    public Injector getInjector() {
        return this.injector;
    }

    @Test
    public void injectorTest() {
        this.injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {

            }

            @Provides
            @Singleton
            public Properties provideProperties() {
                final JsonObject data = JsonParser.parseString("{\n" +
                        "  \"url\": \"jdbc:mysql://localhost:3306/main?useSSL=false&useTimezone=true&serverTimezone=UTC\",\n" +
                        "  \"user\": \"root\",\n" +
                        "  \"password\": \"\",\n" +
                        "  \"minimumIdle\": \"5\",\n" +
                        "  \"maximumPoolSize\": \"50\",\n" +
                        "  \"idleTimeout\": \"30000\",\n" +
                        "  \"connectionTimeout\": \"20000\",\n" +
                        "  \"cachePrepStmts\": \"true\",\n" +
                        "  \"prepStmtCacheSize\": \"250\",\n" +
                        "  \"prepStmtCacheSqlLimit\": \"2048\",\n" +
                        "  \"autoCommit\": true,\n" +
                        "  \"driver\": \"com.mysql.cj.jdbc.Driver\",\n" +
                        "  \"dialect\": \"org.hibernate.dialect.MySQL8Dialect\",\n" +
                        "  \"showSql\": \"true\",\n" +
                        "  \"currentSessionContextClass\": \"thread\",\n" +
                        "  \"hbm2ddlAuto\": \"update\",\n" +
                        "  \"connectionProvider\": \"org.hibernate.hikaricp.internal.HikariCPConnectionProvider\",\n" +
                        "  \"secondLevelCache\": \"true\",\n" +
                        "  \"cacheStructureEntries\": \"true\",\n" +
                        "  \"cacheFactoryClass\": \"org.hibernate.cache.ehcache.internal.EhcacheRegionFactory\",\n" +
                        "  \"cacheProviderClass\": \"net.sf.ehcache.hibernate.EhCacheProvider\",\n" +
                        "  \"cacheQueryCache\": \"true\"\n" +
                        "}").getAsJsonObject();
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
                settings.put("hibernate.hikari.cachePrepStmts", fun.apply("cachePrepStmts"));
                settings.put("hibernate.hikari.prepStmtCacheSize", fun.apply("prepStmtCacheSize"));
                settings.put("hibernate.hikari.prepStmtCacheSqlLimit", fun.apply("prepStmtCacheSqlLimit"));
                settings.put("hibernate.hikari.autoCommit", fun.apply("autoCommit"));
                settings.put("net.sf.ehcache.configurationResourceName", "/META-INF/ehcache.xml");
                return settings;
            }

            @Provides
            @Singleton
            public SessionFactory providesSessionFactory(Properties properties) {
                final Configuration configuration = new Configuration();
                configuration.setProperties(properties);
                //todo core.getEntitiesClass().forEach(configuration::addAnnotatedClass);
                final ServiceRegistry serviceRegistry =
                        new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
                return configuration.buildSessionFactory(serviceRegistry);
            }
        });
        injector.injectMembers(this);
    }
}
