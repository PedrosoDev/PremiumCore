package com.leonardo.premiumcore.database;

import com.google.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class JpaRepository<O, T extends Serializable> implements Repository<O, T> {

    @Getter
    private final Class<O> target;
    @Inject
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private SessionFactory sessionFactory;

    public JpaRepository(Class<O> target) {
        this.target = target;
    }

    @Override
    public void commit(O obj) {
        final Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        session.save(obj);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public Optional<O> read(T id) {
        Optional<O> opt;
        final Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        try {
            final O obj = session.find(target, id);
            session.getTransaction().commit();
            ;
            opt = Optional.of(obj);
        } catch (Exception e) {
            LogManager.getRootLogger().info("Ocorreu um erro ao requisitar no banco de dados a entidade " +
                                            getTarget().getSimpleName() + " de id " + id);
            session.getTransaction().rollback();
            opt = Optional.empty();
        } finally {
            session.close();
        }
        return opt;
    }

    @Override
    public void update(O obj) {
        final Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        session.merge(obj);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void delete(O obj) {
        final Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        session.remove(obj);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void deleteById(T id) {
        final Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        final CriteriaBuilder builder = session.getCriteriaBuilder();
        final CriteriaDelete<O> query = builder.createCriteriaDelete(getTarget());
        final Root<O> root = query.from(getTarget());
        final EntityType<O> entity = session.getMetamodel().entity(getTarget());
        query.where(builder.equal(root.get(entity.getId(getTarget()).getName()), id));
        session.createQuery(query).executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public List<O> findAll() {
        final Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        final List<O> list = session.createQuery("FROM " + target.getName(), target).getResultList();
        session.getTransaction().commit();
        session.close();
        return list;
    }

    @Override
    public boolean exists(T id) {
        final Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        final CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        final CriteriaQuery<O> query = builder.createQuery(getTarget());
        final Root<O> root = query.from(getTarget());
        final EntityType<O> entity = session.getMetamodel().entity(getTarget());
        query.select(root).where(builder.equal(root.get(entity.getId(getTarget()).getName()), id));
        final boolean empty = session.createQuery(query).getResultList().isEmpty();
        session.getTransaction().commit();
        session.close();
        return empty;
    }

}
