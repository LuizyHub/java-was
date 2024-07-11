package codesquad.router;

import codesquad.dao.user.ImmutableUser;
import codesquad.dao.user.User;
import codesquad.dao.user.UserDao;
import server.function.PairAdder;
import server.function.RouterFunction;
import server.http11.HttpMethod;
import server.http11.HttpRequest;
import server.http11.HttpResponse;
import server.http11.HttpStatus;
import server.router.Router;
import server.util.EndPoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersRouter extends Router {

    private final UserDao userDao;

    public UsersRouter(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    protected String setBasePath() {
        return "/user";
    }

    @Override
    protected void addRouterFunctions(PairAdder<EndPoint, RouterFunction> routerFunctionAdder) {
        routerFunctionAdder.add(getGetUsersEndPoint, this::getUsers);
        routerFunctionAdder.add(getUserEndPoint, this::getUser);
        routerFunctionAdder.add(createUserEndPoint, this::createUser);
    }

    private final EndPoint getGetUsersEndPoint = EndPoint.of(HttpMethod.GET, "/list");
    private Map<Long, ImmutableUser> getUsers(HttpRequest request, HttpResponse response) {
        Map<Long, ImmutableUser> users = new HashMap<>();
        List<User> userList = userDao.findAll();
        for (User user : userList) {
            users.put(user.getId(), user.toImmutableUser());
        }

        response.setHeader("Content-Type", "application/json");
        return users;
    }

    private final EndPoint getUserEndPoint = EndPoint.of(HttpMethod.GET, "/{id}");
    private ImmutableUser getUser(HttpRequest request, HttpResponse response) {
        EndPoint endPoint = getUserEndPoint.addBasePath(getBasePath());
        Long userId = Long.parseLong(endPoint.getMatcher(request.endPoint().path()).group(1));
        return userDao.findById(userId).toImmutableUser();
    }

    private final EndPoint createUserEndPoint = EndPoint.of(HttpMethod.POST, "/create");
    private Object createUser(HttpRequest request, HttpResponse response) {
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
        response.setHeader("Location", "/index.html");
        response.setHeader("Content-Type", "application/json");
        return user.toImmutableUser();
    }
}
