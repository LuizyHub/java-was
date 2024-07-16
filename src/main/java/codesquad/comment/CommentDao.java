package codesquad.comment;

import java.util.List;

public interface CommentDao {
    Comment save (Comment comment);

    Comment findById(Long id);

    List<Comment> findByBoardId(Long boardId);

    List<Comment> findAll();

    void deleteAll();
}
