package codesquad.board;

import java.util.List;

public interface BoardDao {
    Board save(Board board);

    Board findById(Long id);

    List<Board> findAll();

    void deleteAll();
}
