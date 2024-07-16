package codesquad.board;

import java.util.List;

public interface BoardDao {
    Board save(Board board);

    /**
     * id에 해당하는 Board를 반환한다.
     * id에 해당하는 Board가 없다면 null을 반환한다.
     * @param id
     * @return Board or null
     */
    Board findById(Long id);

    List<Board> findAll();

    void deleteAll();
}
