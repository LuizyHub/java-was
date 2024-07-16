package codesquad.comment;

import org.h2.jdbcx.JdbcConnectionPool;

class H2CommentDaoTest extends CommentDaoTest {
    private final JdbcConnectionPool pool = JdbcConnectionPool.create("jdbc:h2:mem:test", "sa", "");

    @Override
    protected CommentDao createCommentDao() {
        return new H2CommentDao(pool);
    }
}
