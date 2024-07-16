package codesquad.board;

import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private final String INSERT_BOARD_SQL = "INSERT INTO boards (title, content) VALUES (?, ?)";

    private Board createBoard(Board board) {
        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(INSERT_BOARD_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, board.getTitle());
            preparedStatement.setString(2, board.getContent());
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

    private final String UPDATE_BOARD_SQL = "UPDATE boards SET title = ?, content = ? WHERE id = ?";
    private Board updateBoard(Board board) {
        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(UPDATE_BOARD_SQL)) {

            preparedStatement.setString(1, board.getTitle());
            preparedStatement.setString(2, board.getContent());
            preparedStatement.setLong(3, board.getId());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Failed to update board, no rows affected.");
            }

            return board;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Board findById(Long id) {
        return null;
    }

    @Override
    public List<Board> findAll() {
        return List.of();
    }

    @Override
    public void deleteAll() {

    }

    private Connection getConnection() throws SQLException {
        return pool.getConnection();
    }

    private final String CREATE_BOARDS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS boards (id BIGINT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(100), content VARCHAR(1000))";
    private void createBoardsTable() {
        try (Connection con = getConnection()) {
            con.createStatement().execute(CREATE_BOARDS_TABLE_SQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
