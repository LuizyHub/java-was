package codesquad.router;

import codesquad.dao.user.User;
import codesquad.dao.user.UserDao;
import codesquad.template.TemplateLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.function.PairAdder;
import server.function.RouterFunction;
import server.http11.HttpMethod;
import server.http11.HttpRequest;
import server.http11.HttpResponse;
import server.router.Router;
import server.session.Session;
import server.session.SessionManager;
import server.util.EndPoint;

public class TemplateRouter extends Router {
    private static final Logger log = LoggerFactory.getLogger(TemplateRouter.class);
    private final TemplateLoader templateLoader;
    private final SessionManager sessionManager;
    private final UserDao userDao;

    public TemplateRouter(TemplateLoader templateLoader, SessionManager sessionManager, UserDao userDao) {
        this.templateLoader = templateLoader;
        this.sessionManager = sessionManager;
        this.userDao = userDao;
    }

    @Override
    protected void addRouterFunctions(PairAdder<EndPoint, RouterFunction> routerFunctionAdder) {
        routerFunctionAdder.add(mainPage, this::mainPageTemplate);
        routerFunctionAdder.add(userListPage, this::userListPageTemplate);
    }

    private final EndPoint mainPage = EndPoint.of(HttpMethod.GET, "/index.html");
    private Object mainPageTemplate(HttpRequest request, HttpResponse response) {

        response.setHeader("Content-Type", "text/html");

        Long userID = getUserId();
        if (userID == null) {
            String loginBtn = templateLoader.loadTemplate("/loginBtn.html");
            String registerBtn = templateLoader.loadTemplate("/registerBtn.html");
            String template = templateLoader.loadTemplate("/main.html", loginBtn + registerBtn);
            response.setHeader("Content-Length", String.valueOf(template.getBytes().length));
            return template;
        }

        User user = userDao.findById(sessionManager.getSession(false).getUserId());
        String userButtons = getUserButtons(user.getNickname());
        String template = templateLoader.loadTemplate("/main.html", userButtons);
        response.setHeader("Content-Length", String.valueOf(template.getBytes().length));

        return template;
    }

    private String getUserButtons(String userNickname) {
        String nameBtn = templateLoader.loadTemplate("/nameBtn.html", userNickname);
        String logoutBtn = templateLoader.loadTemplate("/logoutBtn.html");
        return nameBtn + logoutBtn;
    }

    private final EndPoint userListPage = EndPoint.of(HttpMethod.GET, "/userList.html");
    private Object userListPageTemplate(HttpRequest request, HttpResponse response) {
        Long userID = getUserId();
        if (userID == null) {
            response.setRedirect("/login");
            return "";
        }

        response.setHeader("Content-Type", "text/html");
        String userList = userDao.findAll().stream()
                .map(user -> templateLoader.loadTemplate("/userLi.html", user.getNickname()))
                .reduce("", (acc, cur) -> acc + cur);
        String userButtons = getUserButtons(userDao.findById(userID).getNickname());
        String template = templateLoader.loadTemplate("/userList.html", userButtons, userList);
        response.setHeader("Content-Length", String.valueOf(template.getBytes().length));
        return template;
    }

    private Long getUserId() {
        Session session = sessionManager.getSession(false);
        if (session == null) {
            return null;
        }
        return session.getUserId();
    }
}
