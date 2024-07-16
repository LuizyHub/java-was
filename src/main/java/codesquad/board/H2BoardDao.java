package codesquad.board;

import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class H2BoardDao implements BoardDao {
    private final JdbcConnectionPool pool;

    public H2BoardDao(JdbcConnectionPool pool) {
        this.pool = pool;
        createBoardsTable();
    }

    @Override
    public Board save(Board board) {
        if (board.getId() == null) {
            return createBoard(board);
        } else {
            return updateBoard(board);
        }
    }

    private final String INSERT_BOARD_SQL = "INSERT INTO boards (user_id, title, content, image_url) VALUES (?, ?, ?, ?)";

    private Board createBoard(Board board) {
        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(INSERT_BOARD_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, board.getUserId());
            preparedStatement.setString(2, board.getTitle());
            preparedStatement.setString(3, board.getContent());
            preparedStatement.setString(4, board.getImageUrl());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Failed to save board, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    board.setId(generatedKeys.getLong(1));
                } else {
                    throw new RuntimeException("Failed to save board, no ID obtained.");
                }
            }

            return board;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final String UPDATE_BOARD_SQL = "UPDATE boards SET user_id = ?, title = ?, content = ?, image_url = ? WHERE id = ?";
    private Board updateBoard(Board board) {
        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(UPDATE_BOARD_SQL)) {
            preparedStatement.setLong(1, board.getUserId());
            preparedStatement.setString(2, board.getTitle());
            preparedStatement.setString(3, board.getContent());
            preparedStatement.setString(4, board.getImageUrl());
            preparedStatement.setLong(5, board.getId());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Failed to update board, no rows affected.");
            }

            return board;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final String FIND_BY_ID_SQL = "SELECT * FROM boards WHERE id = ?";
    @Override
    public Board findById(Long id) {
        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Board(
                        resultSet.getLong("id"),
                        resultSet.getLong("user_id"),
                        resultSet.getString("title"),
                        resultSet.getString("content"),
                        resultSet.getString("image_url"));
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final String FIND_ALL_SQL = "SELECT * FROM boards";
    @Override
    public List<Board> findAll() {
        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(FIND_ALL_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Board> boards = new ArrayList<>();
            while (resultSet.next()) {
                boards.add(new Board(
                        resultSet.getLong("id"),
                        resultSet.getLong("user_id"),
                        resultSet.getString("title"),
                        resultSet.getString("content"),
                        resultSet.getString("image_url")));
            }
            return boards;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final String DELETE_ALL_SQL = "DELETE FROM boards";
    @Override
    public void deleteAll() {
        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(DELETE_ALL_SQL)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() throws SQLException {
        return pool.getConnection();
    }

    private final String CREATE_BOARDS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS boards (id BIGINT AUTO_INCREMENT PRIMARY KEY, user_id BIGINT, title VARCHAR(100), content VARCHAR(1000), image_url VARCHAR(100))";
    private void createBoardsTable() {
        try (Connection con = getConnection()) {
            con.createStatement().execute(CREATE_BOARDS_TABLE_SQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
