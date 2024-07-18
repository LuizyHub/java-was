package codesquad.user;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static csv.CsvUtils.*;

public class CsvUserDao implements UserDao {
    private static final String FILE_NAME = "user.csv";
    public static final String INSERT = "INSERT INTO users (id, user_id, nickname, password) VALUES (%s, %s, %s, %s)";
    private final String url;
    private final Driver driver;

    public CsvUserDao(Driver driver) {
        this.driver = driver;
        this.url = "jdbc:csv:" + getApplicationDirectory() + "/" + FILE_NAME;
        createCsvFileIfNotExists();
    }

    private void createCsvFileIfNotExists() {
        File file = new File(getApplicationDirectory() + "/" + FILE_NAME);
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("id,user_id,nickname,password\n");
            } catch (IOException e) {
                throw new RuntimeException("Failed to create CSV file", e);
            }
        }
    }

    private String getApplicationDirectory() {
        // Get the path of the running JAR file or class
        String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        File jarFile = new File(path);
        // Get the directory
        return jarFile.getParentFile().getAbsolutePath();
    }

    @Override
    public User save(User user) {
        try (Connection connect = getConnection();
             Statement statement = connect.createStatement()
        ) {
            if (user.getId() == null) {
                user.setId(UUID.randomUUID().getMostSignificantBits());
            }
            String sql = String.format(
                    INSERT,
                    user.getId(),
                    encode(user.getUserId()),
                    encode(user.getNickname()),
                    encode(user.getPassword()));
            statement.execute(sql);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User findById(Long id) {
        return findAll().stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public User findByUserId(String userId) {
        return findAll().stream().filter(user -> user.getUserId().equals(userId)).findFirst().orElse(null);
    }

    private final String FIND_ALL_SQL = "SELECT * FROM users";
    @Override
    public List<User> findAll() {
        try (Connection connect = getConnection();
             Statement statement = connect.createStatement();
             ResultSet resultSet = statement.executeQuery(FIND_ALL_SQL);
        ) {
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(new User(
                        Long.parseLong(resultSet.getString(1)),
                        decode(resultSet.getString(2)),
                        decode(resultSet.getString(3)),
                        decode(resultSet.getString(4))
                ));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll() {

    }

    private Connection getConnection() throws SQLException {
        return driver.connect(url, null);
    }
}
