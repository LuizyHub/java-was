package codesquad.board;

import java.util.List;

public interface BoardDao {
    /**
     * Board를 저장하고 저장된 Board를 반환한다.
     * Board의 id가 null이라면 새로운 Board를 생성하고, id가 null이 아니라면 Board를 업데이트한다.
     * @param board
     * @return Board
     */
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
