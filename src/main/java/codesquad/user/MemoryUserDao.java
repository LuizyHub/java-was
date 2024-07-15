package codesquad.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class MemoryUserDao implements UserDao {
    private static final ConcurrentMap<Long, User> database = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(MemoryUserDao.class);
    private static AtomicLong sequence = new AtomicLong(0);

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(sequence.incrementAndGet());
        }
        database.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(Long id) {
        return database.get(id);
    }

    @Override
    public User findByUserId(String userId) {
        log.debug("database: {}", database.values());
        return database.values().stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(database.values());
    }

    @Override
    public void deleteAll() {
        database.clear();
    }
}
