package server;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Luokka hoitaa kaikki Hibernateen kohdistuvat operaatiot
 *
 * @author Tuomas
 */
public class DatabaseInterface {
    private static DatabaseInterface ourInstance = new DatabaseInterface();
    private SessionFactory sessionFactory;

    private DatabaseInterface() {
        System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "true");
        Configuration conf = new Configuration().configure();

        conf.addAnnotatedClass(Statistic.class);
        conf.addAnnotatedClass(util.PlayerHand.class);
        conf.addAnnotatedClass(util.Card.class);
        conf.addAnnotatedClass(util.Player.class);

        ServiceRegistry sr = new StandardServiceRegistryBuilder().applySettings(conf.getProperties()).build();
        sessionFactory = conf.buildSessionFactory(sr);
    }

    public static DatabaseInterface getInstance() {
        return ourInstance;
    }

    public Session startTransaction() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        return session;
    }

    public void commitTransaction(Session session) {
        session.getTransaction().commit();
    }

    public void closeSession(Session session) {
        session.close();
    }

    public void saveStatistic(Statistic stat) {
        Session session = startTransaction();

        session.save(stat);

        commitTransaction(session);
        closeSession(session);
    }

    public List<Statistic> getStatistics() {
        Session session = startTransaction();

        CriteriaBuilder builder = session.getCriteriaBuilder();

        CriteriaQuery<Statistic> query = builder.createQuery(Statistic.class);

        Root<Statistic> statRoot = query.from(Statistic.class);

        query.select(statRoot);

        return session.createQuery(query).getResultList();
    }
}
