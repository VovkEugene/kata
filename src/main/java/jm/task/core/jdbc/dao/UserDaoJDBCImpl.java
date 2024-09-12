package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.exception.DaoException;
import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {
    private static final UserDaoJDBCImpl INSTANCE = new UserDaoJDBCImpl();

    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS users (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                first_name VARCHAR(255) NOT NULL,
                last_name VARCHAR(255) NOT NULL,
                age SMALLINT
            );
            """;
    private static final String DROP_TABLE_SQL = """
            DROP TABLE IF EXISTS users;
            """;
    private static final String SAVE_SQL = """
            INSERT INTO users(first_name, last_name, age)
            VALUES (?, ?, ?);
            """;
    private static final String DELETE_SQL = """
            DELETE FROM users
            WHERE id = ?;
            """;
    private static final String GET_ALL_SQL = """
            SELECT id, first_name, last_name, age
            FROM users;
            """;
    private static final String CLEAN_TABLE_SQL = """
            TRUNCATE TABLE users;
            """;

    private UserDaoJDBCImpl() {

    }

    @Override
    public void createUsersTable() {
        try (Connection connection = Util.openConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_TABLE_SQL)) {
            statement.execute();
        } catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }

    @Override
    public void dropUsersTable() {
        try (Connection connection = Util.openConnection();
             PreparedStatement statement = connection.prepareStatement(DROP_TABLE_SQL)) {
            statement.execute();
        } catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        try (Connection connection = Util.openConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setString(2, lastName);
            statement.setByte(3, age);
            statement.executeUpdate();
            System.out.printf("User с именем Ч %s добавлен в базу данных\n", name);
        } catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }

    @Override
    public void removeUserById(long id) {
        try (Connection connection = Util.openConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }

    @Override
    public List<User> getAllUsers() {
        try (Connection connection = Util.openConnection();
             PreparedStatement statement = connection.prepareStatement(GET_ALL_SQL)) {
            List<User> users = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getByte("age")));
            }
            users.forEach(System.out::println);
            return users;
        } catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }

    @Override
    public void cleanUsersTable() {
        try (Connection connection = Util.openConnection();
             PreparedStatement statement = connection.prepareStatement(CLEAN_TABLE_SQL)) {
            statement.execute();
        } catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }

    public static UserDaoJDBCImpl getInstance() {
        return INSTANCE;
    }
}
