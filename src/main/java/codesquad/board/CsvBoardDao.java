package codesquad.board;

import csv.CsvUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static csv.CsvUtils.*;

public class CsvBoardDao implements BoardDao {
    private static final String FILE_NAME = "boards.csv";
    public static final String INSERT = "INSERT INTO boards (id, user_id, title, content, image_url) VALUES (%s, %s, %s, %s, %s)";
    private final String URL;
    private final Driver driver;

    public CsvBoardDao(Driver driver, String dir) {
        this.driver = driver;
        this.URL = "jdbc:csv:" + getApplicationDirectory() + dir + "/" + FILE_NAME;
        createCsvFileIfNotExists();
    }

    private void createCsvFileIfNotExists() {
        File file = new File(URL.substring(9));
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("id,user_id,title,content,image_url\n");
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
    public Board save(Board board) {
        if (board.getId() == null) {
            board.setId(UUID.randomUUID().getMostSignificantBits());
        }
        try (Connection connect = getConnection();
             Statement statement = connect.createStatement()) {

            board.setId(UUID.randomUUID().getMostSignificantBits());
            String sql = String.format(INSERT,
                    board.getId(),
                    board.getUserId(),
                    encode(board.getTitle()),
                    encode(board.getContent()),
                    encode(board.getImageUrl()));
            statement.execute(sql);
            return board;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Board createBoard(Board board) {
        try (Connection connect = getConnection();
             Statement statement = connect.createStatement()) {

            board.setId(UUID.randomUUID().getMostSignificantBits());
            String sql = String.format(INSERT,
                    board.getId(),
                    board.getUserId(),
                    encode(board.getTitle()),
                    encode(board.getContent()),
                    encode(board.getImageUrl())
            );
            statement.execute(sql);
            return board;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Board findById(Long id) {
        return findAll().stream().filter(board -> board.getId().equals(id)).findFirst().orElse(null);
    }

    private final String FIND_ALL_SQL = "SELECT * FROM boards";

    @Override
    public List<Board> findAll() {
        try (Connection connect = getConnection();
             Statement statement = connect.createStatement();
             ResultSet resultSet = statement.executeQuery(FIND_ALL_SQL)) {
            List<Board> boards = new ArrayList<>();
            while (resultSet.next()) {
                boards.add(new Board(
                        Long.parseLong(resultSet.getString(1)),
                        Long.parseLong(resultSet.getString(2)),
                        decode(resultSet.getString(3)),
                        decode(resultSet.getString(4)),
                        decode(resultSet.getString(5))
                ));
            }
            return boards;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void deleteAll() {
    }

    private Connection getConnection() throws SQLException {
        return driver.connect(URL, null);
    }
}
