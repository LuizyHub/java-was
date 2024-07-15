package codesquad.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class UserDaoTest {
    protected UserDao userDao;

    protected abstract UserDao createUserDao();

    @BeforeAll
    void setUpClass() {
        userDao = createUserDao();
    }

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
        String userId = "luizyToBeFound";
        User user = new User(userId, "luizy", "1234");
        User savedUser = userDao.save(user);

        // when
        User foundUser = userDao.findByUserId(userId);

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