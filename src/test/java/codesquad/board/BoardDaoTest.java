package codesquad.board;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BoardDaoTest {
    protected BoardDao boardDao;

    protected abstract BoardDao createBoardDao();

    @BeforeAll
    void setUpClass() {
        boardDao = createBoardDao();
    }

    @AfterEach
    void tearDown() {
        boardDao.deleteAll();
    }

    @Test
    void create() {
        // given
        Board board = new Board(1L,"title", "contents, 좋아요", " ");

        // when
        Board savedBoard = boardDao.save(board);

        // then
        assertNotNull(savedBoard.getId());
    }

    @Test
    void findById() {
        // given
        Board board = new Board(1L, "title", "contents", " ");
        Board savedBoard = boardDao.save(board);

        // when
        Board foundBoard = boardDao.findById(savedBoard.getId());

        // then
        assertEquals(savedBoard.getId(), foundBoard.getId());
        assertEquals(savedBoard.getTitle(), foundBoard.getTitle());
        assertEquals(savedBoard.getContent(), foundBoard.getContent());
    }

    @Test
    void deleteAll() {
        // given
        Board board = new Board(1L, "title", "contents", " ");
        boardDao.save(board);

        // when
        boardDao.deleteAll();

        // then
        assertEquals(0, boardDao.findAll().size());
    }

    @Test
    void findAll() {
        // given
        int sizeBefore = boardDao.findAll().size();
        Board board1 = new Board(1L, "title1", "contents1", " ");
        Board board2 = new Board(1L, "title2", "contents2", " ");
        boardDao.save(board1);
        boardDao.save(board2);

        // when
        int size = boardDao.findAll().size();

        // then
        assertEquals(2 + sizeBefore, size);
    }


}
