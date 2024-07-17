package codesquad.user;

import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class H2UserDao implements UserDao {
    private final JdbcConnectionPool pool;

    public H2UserDao(JdbcConnectionPool pool) {
        this.pool = pool;
        createUsersTable();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            return createUser(user);
        } else {
            return updateUser(user);
        }
    }

    private final String INSERT_USER_SQL = "INSERT INTO users (user_id, nickname, password) VALUES (?, ?, ?)";
    private User createUser(User user) {
        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, user.getUserId());
            preparedStatement.setString(2, user.getNickname());
            preparedStatement.setString(3, user.getPassword());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Failed to save user, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new RuntimeException("Failed to save user, no ID obtained.");
                }
            }

            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final String UPDATE_USER_SQL = "UPDATE users SET user_id = ?, nickname = ?, password = ? WHERE id = ?";
    private User updateUser(User user) {
        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(UPDATE_USER_SQL)) {

            preparedStatement.setString(1, user.getUserId());
            preparedStatement.setString(2, user.getNickname());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setLong(4, user.getId());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Failed to update user, no rows affected.");
            }

            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final String SELECT_USER_BY_ID_SQL = "SELECT * FROM users WHERE id = ?";
    @Override
    public User findById(Long id) {
        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(SELECT_USER_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getLong("id"),
                            resultSet.getString("user_id"),
                            resultSet.getString("nickname"),
                            resultSet.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private final String SELECT_USER_BY_USER_ID_SQL = "SELECT * FROM users WHERE user_id = ?";
    @Override
    public User findByUserId(String userId) {
        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(SELECT_USER_BY_USER_ID_SQL)) {

            preparedStatement.setString(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getLong("id"),
                            resultSet.getString("user_id"),
                            resultSet.getString("nickname"),
                            resultSet.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private final String SELECT_ALL_USERS_SQL = "SELECT * FROM users";
    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection con = getConnection();
             Statement statement = con.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_ALL_USERS_SQL)) {

            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getLong("id"),
                        resultSet.getString("user_id"),
                        resultSet.getString("nickname"),
                        resultSet.getString("password")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    private final String DELETE_ALL_USERS_SQL = "DELETE FROM users";
    @Override
    public void deleteAll() {
        try (Connection con = getConnection();
             Statement statement = con.createStatement()) {

            statement.execute(DELETE_ALL_USERS_SQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() {
        try {
            return pool.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final String CREATE_USERS_TABLE_IF_NOT_EXISTS_SQL = "CREATE TABLE IF NOT EXISTS users (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "user_id VARCHAR(255), " +
            "nickname VARCHAR(255), " +
            "password VARCHAR(255))";
    private void createUsersTable() {
        try (Connection con = getConnection();
             Statement statement = con.createStatement()) {
            statement.execute(CREATE_USERS_TABLE_IF_NOT_EXISTS_SQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
