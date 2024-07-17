package codesquad.database;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class H2Test {

    private static Connection connection;

    @BeforeAll
    public static void h2() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
        Statement statement = connection.createStatement();

        // users 테이블 생성
        String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "email VARCHAR(255))";
        statement.execute(createUsersTableSQL);
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        Statement statement = connection.createStatement();
        String dropUsersTableSQL = "DROP TABLE users";
        statement.execute(dropUsersTableSQL);
        statement.close();
        connection.close();
    }

    @BeforeEach
    public void insertTestData() throws SQLException {
        String insertUserSQL = "INSERT INTO users (name, email) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertUserSQL)) {
            preparedStatement.setString(1, "John Doe");
            preparedStatement.setString(2, "john@example.com");
            preparedStatement.executeUpdate();
        }
    }

    @AfterEach
    public void clearTestData() throws SQLException {
        String deleteUserSQL = "DELETE FROM users WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteUserSQL)) {
            preparedStatement.setString(1, "John Doe");
            preparedStatement.executeUpdate();
        }
    }

    @Test
    public void testInsertUser() throws SQLException {
        String insertUserSQL = "INSERT INTO users (name, email) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertUserSQL)) {
            preparedStatement.setString(1, "John Doe");
            preparedStatement.setString(2, "john@example.com");
            int rowsAffected = preparedStatement.executeUpdate();
            assertEquals(1, rowsAffected);
        }
    }

    @Test
    public void testSelectUser() throws SQLException {
        String selectUserSQL = "SELECT * FROM users WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectUserSQL)) {
            preparedStatement.setString(1, "John Doe");
            ResultSet resultSet = preparedStatement.executeQuery();
            assertTrue(resultSet.next());
            assertEquals("John Doe", resultSet.getString("name"));
            assertEquals("john@example.com", resultSet.getString("email"));
        }
    }

    @Test
    public void testUpdateUser() throws SQLException {
        String updateUserSQL = "UPDATE users SET email = ? WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateUserSQL)) {
            preparedStatement.setString(1, "john.doe@example.com");
            preparedStatement.setString(2, "John Doe");
            int rowsAffected = preparedStatement.executeUpdate();
            assertEquals(1, rowsAffected);
        }
    }

    @Test
    public void testDeleteUser() throws SQLException {
        String deleteUserSQL = "DELETE FROM users WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteUserSQL)) {
            preparedStatement.setString(1, "John Doe");
            int rowsAffected = preparedStatement.executeUpdate();
            assertEquals(1, rowsAffected);
        }
    }
}
