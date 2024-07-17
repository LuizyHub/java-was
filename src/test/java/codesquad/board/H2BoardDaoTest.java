package codesquad.board;

import org.h2.jdbcx.JdbcConnectionPool;

class H2BoardDaoTest extends BoardDaoTest {
    private final JdbcConnectionPool pool = JdbcConnectionPool.create("jdbc:h2:mem:test", "sa", "");

    @Override
    protected BoardDao createBoardDao() {
        return new H2BoardDao(pool);
    }
}