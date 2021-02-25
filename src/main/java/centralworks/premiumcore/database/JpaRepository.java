package centralworks.premiumcore.database;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JpaRepository<O, T extends Serializable> implements Repository<O, T> {

    @Setter
    @Getter
    @Inject
    private Session session;
    @Setter
    @Getter
    @Inject
    private EntityManager em;
    @Getter
    private final Class<O> target;

    public JpaRepository(Class<O> target) {
        this.target = target;
    }

    @Override
    public void commit(O obj) {
        try {
            update(obj);
        } catch (Exception ignored) {
            final Transaction transaction = session.beginTransaction();
            session.save(obj);
            transaction.commit();
        }
    }

    @Override
    public Optional<O> read(T id) {
        final Transaction transaction = session.beginTransaction();
        try {
            final O obj = session.load(target, id);
            transaction.commit();
            return Optional.of(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        transaction.commit();
        return Optional.empty();
    }

    @Override
    public void update(O obj) {
        final Transaction transaction = session.beginTransaction();
        try {
            session.update(obj);
        } catch (Exception ignored) {
        }
        transaction.commit();
    }

    @Override
    public void delete(O obj) {
        final Transaction transaction = session.beginTransaction();
        try {
            session.remove(obj);
        } catch (Exception ignored) {
        }
        transaction.commit();
    }

    @Override
    public Optional<O> findAndDelete(T id) {
        final Transaction transaction = session.beginTransaction();
        final Optional<O> o = Optional.of(session.load(target, id));
        o.ifPresent(session::delete);
        transaction.commit();
        return o;
    }

    @Override
    public List<O> findAll() {
        return session.createQuery("FROM " + target.getName(), target).getResultList();
    }

    @Override
    public List<O> findAll(Predicate<O> predicate) {
        return findAll().stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public boolean exists(T id) {
        final Transaction transaction = session.beginTransaction();
        boolean result = false;
        try {
            session.load(target, id);
            result = true;
        } catch (Exception ignored) {
        }
        transaction.commit();
        return result;
    }

    @Override
    public TypedQuery<O> createQuery(String query) {
        return session.createQuery(query, target);
    }

    @Override
    public TypedQuery<O> createQuery(CriteriaQuery<O> criteriaQuery) {
        return session.createQuery(criteriaQuery);
    }

    @Override
    public Query createNativeQuery(String query) {
        return session.createNativeQuery(query, target);
    }
}
