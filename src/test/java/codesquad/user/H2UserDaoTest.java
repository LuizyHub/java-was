package codesquad.user;

import org.h2.jdbcx.JdbcConnectionPool;

class H2UserDaoTest extends UserDaoTest {
    private static final JdbcConnectionPool pool = JdbcConnectionPool.create("jdbc:h2:mem:test", "sa", "");

    @Override
    protected UserDao createUserDao() {
        return new H2UserDao(pool);
    }
}