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
import server.session.Session;
import server.session.SessionManager;
import server.util.EndPoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersRouter extends Router {
    private static final String LOGIN_FAILED_HTML = "/user/login_failed.html";

    private final SessionManager sessionManager;
    private final UserDao userDao;

    public UsersRouter(SessionManager sessionManager, UserDao userDao) {
        this.sessionManager = sessionManager;
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
        routerFunctionAdder.add(loginEndPoint, this::login);
        routerFunctionAdder.add(getUserNameEndPoint, this::getUserName);
    }

    private final EndPoint getGetUsersEndPoint = EndPoint.of(HttpMethod.GET, "/list");
    private Object getUsers(HttpRequest request, HttpResponse response) {
        response.setRedirect("/userList.html");
        return "";
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
        response.setRedirect("/index.html");
        response.setHeader("Content-Type", "application/json");
        return user.toImmutableUser();
    }

    private final EndPoint loginEndPoint = EndPoint.of(HttpMethod.POST, "/login");
    private Object login(HttpRequest request, HttpResponse response) {
        Map<String, List<String>> queryParams = request.queryParams();
        if (!queryParams.containsKey("userId") || !queryParams.containsKey("password")) {
            response.setRedirect(LOGIN_FAILED_HTML);
            return "required parameters are missing";
        }
        String userId = queryParams.get("userId").get(0);
        String password = queryParams.get("password").get(0);
        User user = userDao.findByUserId(userId);
        if (user == null || !user.getPassword().equals(password)) {
            response.setRedirect(LOGIN_FAILED_HTML);
            return "login failed";
        }
        Session session = sessionManager.getSession();
        session.setUserId(user.getId());
        response.setRedirect("/index.html");
        return user.toImmutableUser();
    }

    private final EndPoint getUserNameEndPoint = EndPoint.of(HttpMethod.GET, "/name");
    private String getUserName(HttpRequest request, HttpResponse response) {
        Session session = sessionManager.getSession(false);
        if (session == null) {
            return "";
        }
        Long userId = session.getUserId();
        User user = userDao.findById(userId);
        return user.getNickname();
    }
}
