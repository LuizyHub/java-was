package codesquad.user;

import java.util.List;

public interface UserDao {

    User save(User user);

    User findById(Long id);

    User findByUserId(String userId);

    List<User> findAll();
}
