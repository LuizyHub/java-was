package codesquad.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MemoryUserDaoTest {

    private final UserDao userDao = new MemoryUserDao();

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
}