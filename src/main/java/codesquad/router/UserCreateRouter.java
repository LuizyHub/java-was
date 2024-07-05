package codesquad.router;

import codesquad.dao.user.User;
import codesquad.dao.user.UserDao;
import codesquad.http11.HttpStatus;

import java.util.List;
import java.util.Map;

public class UserCreateRouter extends Router{
    private static final UserCreateRouter instance = new UserCreateRouter();
    private UserCreateRouter() {}
    public static UserCreateRouter getInstance() {return instance;}

    private final UserDao userDao = UserDao.getInstance();


    {
        path = "/create";

        get = (request, response) -> {
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
            response.setStatus(HttpStatus.FOUND);
            response.setHeader("Location", "/main");
            return user.toImmutableUser();
        };
    }
}
