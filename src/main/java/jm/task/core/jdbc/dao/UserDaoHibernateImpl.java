package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.exception.DaoException;
import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class UserDaoHibernateImpl implements UserDao {
    private static final UserDaoHibernateImpl INSTANCE = new UserDaoHibernateImpl();
    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS users (                                  
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                first_name VARCHAR(255) NOT NULL,
                last_name VARCHAR(255) NOT NULL,
                age SMALLINT);
            """;
    private static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS users";
    private static final String GET_ALL_HQL = "FROM User";
    private static final String CLEAN_TABLE_HQL = "DELETE FROM User";

    private Transaction transaction = null;


    private UserDaoHibernateImpl() {
    }

    @Override
    public void createUsersTable() {
        openTransaction(CREATE_TABLE_SQL);
    }

    @Override
    public void dropUsersTable() {
        openTransaction(DROP_TABLE_SQL);
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        try (SessionFactory factory = Util.getSessionFactory();
             Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            session.persist(new User(name, lastName, age));
            session.getTransaction().commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DaoException(e);
        }
    }

    @Override
    public void removeUserById(long id) {
        try (SessionFactory factory = Util.getSessionFactory();
             Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            User user = session.get(User.class, id);
            session.remove(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DaoException(e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        try (SessionFactory factory = Util.getSessionFactory();
             Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            List<User> list = session.createQuery(GET_ALL_HQL, User.class).list();
            session.getTransaction().commit();
            return list;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DaoException(e);
        }
    }

    @Override
    public void cleanUsersTable() {
        try (SessionFactory factory = Util.getSessionFactory();
             Session session = factory.getCurrentSession()) {
            transaction = session.beginTransaction();
            session.createQuery(CLEAN_TABLE_HQL).executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DaoException(e);
        }
    }

    public static UserDaoHibernateImpl getInstance() {
        return INSTANCE;
    }

    private void openTransaction(String sqlQuery) {
        try (SessionFactory factory = Util.getSessionFactory();
             Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            session.createNativeQuery(sqlQuery, User.class).executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DaoException(e);
        }
    }
}
