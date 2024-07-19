package codesquad.comment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static csv.CsvUtils.*;

public class CsvCommentDao implements CommentDao {
    private static final String FILE_NAME = "comments.csv";
    public static final String INSERT = "INSERT INTO comments (id, user_id, board_id, content) VALUES (%s, %s, %s, %s)";
    private final String URL;
    private final Driver driver;

    public CsvCommentDao(Driver driver, String dir) {
        this.driver = driver;
        this.URL = "jdbc:csv:" + getApplicationDirectory() + dir + "/" + FILE_NAME;
        createCsvFileIfNotExists();
    }

    private void createCsvFileIfNotExists() {
        File file = new File(URL.substring(9));
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("id,user_id,board_id,content\n");
            } catch (IOException e) {
                throw new RuntimeException("Failed to create CSV file", e);
            }
        }
    }

    private String getApplicationDirectory() {
        String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        File jarFile = new File(path);
        return jarFile.getParentFile().getAbsolutePath();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            comment.setId(UUID.randomUUID().getMostSignificantBits());
        }
        try (Connection connect = getConnection();
             Statement statement = connect.createStatement()) {

            comment.setId(UUID.randomUUID().getMostSignificantBits());
            String sql = String.format(INSERT,
                    comment.getId(),
                    comment.getUserId(),
                    comment.getBoardId(),
                    encode(comment.getContent()));
            statement.execute(sql);
            return comment;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Comment findById(Long id) {
        return findAll().stream().filter(comment -> comment.getId().equals(id)).findFirst().orElse(null);
    }

    private final String FIND_ALL_SQL = "SELECT * FROM comments";
    @Override
    public List<Comment> findAll() {
        try (Connection connect = getConnection();
             Statement statement = connect.createStatement();
             ResultSet resultSet = statement.executeQuery(FIND_ALL_SQL)) {
            List<Comment> comments = new ArrayList<>();
            while (resultSet.next()) {
                comments.add(new Comment(
                        Long.parseLong(resultSet.getString(1)),
                        Long.parseLong(resultSet.getString(2)),
                        Long.parseLong(resultSet.getString(3)),
                        decode(resultSet.getString(4))
                ));
            }
            return comments;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Comment> findByBoardId(Long boardId) {
        return findAll().stream().filter(comment -> comment.getBoardId().equals(boardId)).toList();
    }

    @Override
    public void deleteAll() {
    }

    public void deleteById(Long id) {
    }

    private List<String[]> toCsvFormat(List<Comment> comments) {
        List<String[]> data = new ArrayList<>();
        for (Comment comment : comments) {
            String[] row = {
                    comment.getId().toString(),
                    comment.getUserId().toString(),
                    comment.getBoardId().toString(),
                    comment.getContent()
            };
            data.add(row);
        }
        return data;
    }

    private Connection getConnection() throws SQLException {
        return driver.connect(URL, null);
    }
}
