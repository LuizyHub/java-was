package codesquad.comment;

import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class H2CommentDao implements CommentDao {
    private final JdbcConnectionPool pool;

    public H2CommentDao(JdbcConnectionPool pool) {
        this.pool = pool;
        createCommentsTable();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            return createComment(comment);
        } else {
            return updateComment(comment);
        }
    }

    private final String INSERT_COMMENT_SQL = "INSERT INTO comments (user_id, board_id, content) VALUES (?, ?, ?)";
    private Comment createComment(Comment comment) {
        try (Connection con = getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(INSERT_COMMENT_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, comment.getUserId());
            preparedStatement.setLong(2, comment.getBoardId());
            preparedStatement.setString(3, comment.getContent());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Failed to save comment, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    comment.setId(generatedKeys.getLong(1));
                } else {
                    throw new RuntimeException("Failed to save comment, no ID obtained.");
                }
            }

            return comment;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private final String UPDATE_COMMENT_SQL = "UPDATE comments SET user_id = ?, board_id = ?, content = ? WHERE id = ?";
    private Comment updateComment(Comment comment) {
        try (Connection con = getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(UPDATE_COMMENT_SQL)) {

            preparedStatement.setLong(1, comment.getUserId());
            preparedStatement.setLong(2, comment.getBoardId());
            preparedStatement.setString(3, comment.getContent());
            preparedStatement.setLong(4, comment.getId());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Failed to update comment, no rows affected.");
            }

            return comment;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final String SELECT_COMMENT_BY_ID_SQL = "SELECT * FROM comments WHERE id = ?";
    @Override
    public Comment findById(Long id) {
        try (Connection con = getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(SELECT_COMMENT_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Comment(
                    resultSet.getLong("id"),
                    resultSet.getLong("user_id"),
                    resultSet.getLong("board_id"),
                    resultSet.getString("content")
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final String SELECT_COMMENTS_BY_BOARD_ID_SQL = "SELECT * FROM comments WHERE board_id = ?";
    @Override
    public List<Comment> findByBoardId(Long boardId) {
        try (Connection con = getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(SELECT_COMMENTS_BY_BOARD_ID_SQL)) {

            preparedStatement.setLong(1, boardId);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Comment> comments = new ArrayList<>();
            while (resultSet.next()) {
                comments.add(new Comment(
                    resultSet.getLong("id"),
                    resultSet.getLong("user_id"),
                    resultSet.getLong("board_id"),
                    resultSet.getString("content")
                ));
            }

            return comments;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final String SELECT_ALL_COMMENTS_SQL = "SELECT * FROM comments";
    @Override
    public List<Comment> findAll() {
        try (Connection con = getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(SELECT_ALL_COMMENTS_SQL);
            ResultSet resultSet = preparedStatement.executeQuery()) {

            List<Comment> comments = new ArrayList<>();
            while (resultSet.next()) {
                comments.add(new Comment(
                    resultSet.getLong("id"),
                    resultSet.getLong("user_id"),
                    resultSet.getLong("board_id"),
                    resultSet.getString("content")
                ));
            }

            return comments;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private final String DELETE_ALL_COMMENTS_SQL = "DELETE FROM comments";
    @Override
    public void deleteAll() {
        try (Connection con = getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(DELETE_ALL_COMMENTS_SQL)) {

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() throws SQLException {
        return pool.getConnection();
    }

    private final String CREATE_COMMENTS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS comments (" +
            "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
            "user_id BIGINT NOT NULL, " +
            "board_id BIGINT NOT NULL, " +
            "content TEXT NOT NULL) ";
    private void createCommentsTable() {
        try (Connection con = getConnection()) {
            con.createStatement().execute(CREATE_COMMENTS_TABLE_SQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
