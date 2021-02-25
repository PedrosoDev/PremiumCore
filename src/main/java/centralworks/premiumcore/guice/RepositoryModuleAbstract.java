package centralworks.premiumcore.guice;

import centralworks.premiumcore.PremiumCore;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Properties;

public abstract class RepositoryModuleAbstract extends AbstractModule {

    private static final ThreadLocal<EntityManager> ENTITY_MANAGER_CACHE = new ThreadLocal<>();
    private static final ThreadLocal<Session> SESSION_CACHE = new ThreadLocal<>();

    @Override
    protected void configure() {
    }

    public List<Class<?>> mappedClasses() {
        return Lists.newArrayList();
    }

    static {
        Thread.currentThread().setContextClassLoader(PremiumCore.class.getClassLoader());
    }

    @Provides
    @Singleton
    public SessionFactory provideSessionFactory(Properties settings) {
        final Configuration configuration = new Configuration();
        configuration.setProperties(settings);
        mappedClasses().forEach(configuration::addAnnotatedClass);
        final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    @Provides
    public Session provideSessionManager(SessionFactory sessionFactory) {
        Session session = SESSION_CACHE.get();
        if (session == null) SESSION_CACHE.set(session = sessionFactory.openSession());
        return session;
    }

    @Provides
    public EntityManager provideEntityManager(SessionFactory sessionFactory) {
        EntityManager entityManager = ENTITY_MANAGER_CACHE.get();
        if (entityManager == null) ENTITY_MANAGER_CACHE.set(entityManager = sessionFactory.createEntityManager());
        return entityManager;
    }
}
