package codesquad.user;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class H2UserDaoTest {
    private static final JdbcConnectionPool pool = JdbcConnectionPool.create("jdbc:h2:mem:test", "sa", "");

    private final H2UserDao userDao = new H2UserDao(pool);

    @Test
    void create() throws InterruptedException {
        // given
        User user = new User("luizy", "luizy", "1234");

        // when
        User savedUser = userDao.save(user);

        // then
        assertNotNull(savedUser.getId());
    }

    @Test
    void update() {
        // given
        User user = new User("luizy", "luizy", "1234");
        User savedUser = userDao.save(user);

        // when
        String newNickname = "luizy2";
        savedUser.setNickname(newNickname);

        // then
        User updatedUser = userDao.save(savedUser);
        assertEquals(newNickname, updatedUser.getNickname());
    }

}