package codesquad.comment;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class CommentDaoTest {
    protected CommentDao commentDao;

    protected abstract CommentDao createCommentDao();

    @BeforeAll
    void setUpClass() {
        commentDao = createCommentDao();
    }

    @AfterEach
    void tearDown() {
        commentDao.deleteAll();
    }

    @Test
    void create() {
        // given
        Comment comment = new Comment(null, 1L, 1L, "This is a test comment.");

        // when
        Comment savedComment = commentDao.save(comment);

        // then
        assertNotNull(savedComment.getId());
        assertEquals(comment.getUserId(), savedComment.getUserId());
        assertEquals(comment.getBoardId(), savedComment.getBoardId());
        assertEquals(comment.getContent(), savedComment.getContent());
    }

    @Test
    void findById() {
        // given
        Comment comment = new Comment(null, 1L, 1L, "This is a test comment.");
        Comment savedComment = commentDao.save(comment);

        // when
        Comment foundComment = commentDao.findById(savedComment.getId());

        // then
        assertNotNull(foundComment);
        assertEquals(savedComment.getId(), foundComment.getId());
        assertEquals(savedComment.getUserId(), foundComment.getUserId());
        assertEquals(savedComment.getBoardId(), foundComment.getBoardId());
        assertEquals(savedComment.getContent(), foundComment.getContent());
    }

    @Test
    void update() {
        // given
        Comment comment = new Comment(null, 1L, 1L, "This is a test comment.");
        Comment savedComment = commentDao.save(comment);

        // when
        savedComment.setContent("Updated comment content.");
        Comment updatedComment = commentDao.save(savedComment);

        // then
        assertNotNull(updatedComment);
        assertEquals(savedComment.getId(), updatedComment.getId());
        assertEquals("Updated comment content.", updatedComment.getContent());
    }

    @Test
    void deleteAll() {
        // given
        Comment comment = new Comment(null, 1L, 1L, "This is a test comment.");
        commentDao.save(comment);

        // when
        commentDao.deleteAll();

        // then
        assertTrue(commentDao.findAll().isEmpty());
    }

    @Test
    void findAll() {
        // given
        Comment comment1 = new Comment(null, 1L, 1L, "First comment.");
        Comment comment2 = new Comment(null, 2L, 1L, "Second comment.");
        commentDao.save(comment1);
        commentDao.save(comment2);

        // when
        int size = commentDao.findAll().size();

        // then
        assertEquals(2, size);
    }

    @Test
    void findByBoardId() {
        // given
        Comment comment1 = new Comment(null, 1L, 1L, "First comment.");
        Comment comment2 = new Comment(null, 2L, 1L, "Second comment.");
        commentDao.save(comment1);
        commentDao.save(comment2);

        // when
        List<Comment> comments = commentDao.findByBoardId(1L);

        // then
        assertEquals(2, comments.size());
        assertEquals("First comment.", comments.get(0).getContent());
        assertEquals("Second comment.", comments.get(1).getContent());
    }
}
