package codesquad.user;

import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.*;
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
        Connection con = getConnection();
        String userId = user.getUserId();
        String nickname = user.getNickname();
        String password = user.getPassword();

        try (PreparedStatement preparedStatement = con.prepareStatement(INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, nickname);
            preparedStatement.setString(3, password);
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
        Connection con = getConnection();
        String userId = user.getUserId();
        String nickname = user.getNickname();
        String password = user.getPassword();
        Long id = user.getId();

        try (PreparedStatement preparedStatement = con.prepareStatement(UPDATE_USER_SQL)) {
            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, nickname);
            preparedStatement.setString(3, password);
            preparedStatement.setLong(4, id);
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Failed to update user, no rows affected.");
            }

            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User findById(Long id) {
        return null;
    }

    @Override
    public User findByUserId(String userId) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    private Connection getConnection() {
        try {
            return pool.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createUsersTable() {
        Connection con = getConnection();
        try (Statement statement = con.createStatement()) {
            // 기존 테이블이 있으면 삭제
            statement.execute("DROP TABLE IF EXISTS users");

            // 새로운 테이블 생성
            statement.execute("CREATE TABLE users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id VARCHAR(255), " +
                    "nickname VARCHAR(255), " +
                    "password VARCHAR(255))");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
