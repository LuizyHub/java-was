package codesquad.router;

import codesquad.dao.user.ImmutableUser;
import codesquad.dao.user.User;
import codesquad.dao.user.UserDao;
import codesquad.http11.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersRouter extends Router {
    private static final UsersRouter instance = new UsersRouter();
    private UsersRouter() {}
    public static UsersRouter getInstance() {return instance;}

    private final UserDao userDao = UserDao.getInstance();

    {
        path = "/users";

        get = (request, response) -> {
            Map<Long, ImmutableUser> usersMap = new HashMap<>();
            userDao.findAll().forEach(user -> usersMap.put(user.getId(), user.toImmutableUser()));
            return usersMap;
        };

        post = (request, response) -> {
            Map<String, List<String>> queryParams = request.queryParams();
            if (!queryParams.containsKey("userId") || !queryParams.containsKey("password") || !queryParams.containsKey("name")) {
                response.setStatus(HttpStatus.BAD_REQUEST);
                return "required parameters are missing";
            }
            String userId = queryParams.get("userId").get(0);
            String password = queryParams.get("password").get(0);
            String name = queryParams.get("name").get(0);
            User user = new User(userId, name, password);
            user = userDao.save(user);
            return user.toImmutableUser();
        };
    }
}
