package codesquad.user;

import java.util.List;

public interface UserDao {

    public User save(User user);

    public User findById(Long id);

    public User findByUserId(String userId);

    public List<User> findAll();
}
