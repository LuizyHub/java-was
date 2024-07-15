package codesquad.user;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class H2UserDaoTest {
    private static final JdbcConnectionPool pool = JdbcConnectionPool.create("jdbc:h2:mem:test", "sa", "");

    private final UserDao userDao = new H2UserDao(pool);

    @AfterEach
    void tearDown() {
        userDao.deleteAll();
    }

    @Test
    void create() {
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

    @Test
    void findById() {
        // given
        User user = new User("luizy", "luizy", "1234");
        User savedUser = userDao.save(user);

        // when
        User foundUser = userDao.findById(savedUser.getId());

        // then
        assertEquals(savedUser.getId(), foundUser.getId());
        assertEquals(savedUser.getUserId(), foundUser.getUserId());
        assertEquals(savedUser.getNickname(), foundUser.getNickname());
        assertEquals(savedUser.getPassword(), foundUser.getPassword());
    }

    @Test
    void findByUserId() {
        // given
        String UserId = "luizyToBeFind";
        User user = new User(UserId, "luizy", "1234");
        User savedUser = userDao.save(user);

        // when
        User foundUser = userDao.findByUserId(UserId);

        // then
        assertEquals(savedUser.getId(), foundUser.getId());
    }

    @Test
    void findAll() {
        // given
        User user1 = new User("luizy1", "luizy1", "1234");
        User user2 = new User("luizy2", "luizy2", "1234");
        User user3 = new User("luizy3", "luizy3", "1234");
        userDao.save(user1);
        userDao.save(user2);
        userDao.save(user3);

        // when
        List<User> users = userDao.findAll();

        // then
        assertEquals(3, users.size());
    }
}