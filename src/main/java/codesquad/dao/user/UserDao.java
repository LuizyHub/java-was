package codesquad.dao.user;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserDao {
    private static final ConcurrentMap<Long, User> database = new ConcurrentHashMap<>();
    private static AtomicLong sequence = new AtomicLong(0);

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(sequence.incrementAndGet());
        }
        database.put(user.getId(), user);
        return user;
    }

    public User findById(Long id) {
        return database.get(id);
    }

    public User findByUserId(String userId) {
        return database.values().stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public List<User> findAll() {
        return List.copyOf(database.values());
    }
}
