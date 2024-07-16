package codesquad.router;

import codesquad.board.Board;
import codesquad.board.BoardDao;
import codesquad.template.Template;
import codesquad.user.User;
import codesquad.user.UserDao;
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

import java.util.Collections;
import java.util.List;

public class TemplateRouter extends Router {
    private static final Logger log = LoggerFactory.getLogger(TemplateRouter.class);
    private final TemplateLoader templateLoader;
    private final SessionManager sessionManager;
    private final UserDao userDao;
    private final BoardDao boardDao;

    public TemplateRouter(TemplateLoader templateLoader, SessionManager sessionManager, UserDao userDao, BoardDao boardDao) {
        this.templateLoader = templateLoader;
        this.sessionManager = sessionManager;
        this.userDao = userDao;
        this.boardDao = boardDao;
    }

    @Override
    protected void addRouterFunctions(PairAdder<EndPoint, RouterFunction> routerFunctionAdder) {
        routerFunctionAdder.add(mainPage, this::mainPageTemplate);
        routerFunctionAdder.add(userListPage, this::userListPageTemplate);
        routerFunctionAdder.add(writePage, this::writePageTemplate);
    }

    private final EndPoint mainPage = EndPoint.of(HttpMethod.GET, "/index.html");
    private Object mainPageTemplate(HttpRequest request, HttpResponse response) {

        response.setHeader("Content-Type", "text/html");

        List<Board> boards = boardDao.findAll();
        boards.sort((a, b) -> b.getId().compareTo(a.getId()));
        List<Template.Post> posts = boards.stream()
                .map(board -> new Template.Post(userDao.findById(board.getUserId()).getNickname(), board.getTitle(), board.getImageUrl(), board.getContent()))
                .toList();

        Long userID = getUserId();
        if (userID == null) {

            Template.NonUserIndex nonUserIndex = new Template.NonUserIndex(posts);
            return nonUserIndex;
        }

        User user = userDao.findById(userID);

        Template.NameBtn nameBtn = new Template.NameBtn(user.getNickname());
        Template.LogoutBtn logoutBtn = new Template.LogoutBtn();
        Template.UserBtn userBtn = new Template.UserBtn(nameBtn, logoutBtn);


        Template.UserIndex userIndex = new Template.UserIndex(userBtn, posts);

        return userIndex;
    }

    private String getUserButtons(String userNickname) {
        String nameBtn = templateLoader.loadTemplate("/nameBtn.html", userNickname);
        String logoutBtn = templateLoader.loadTemplate("/logoutBtn.html");
        return nameBtn + logoutBtn;
    }

    private final EndPoint userListPage = EndPoint.of(HttpMethod.GET, "/userList.html");
    private Template.UserList userListPageTemplate(HttpRequest request, HttpResponse response) {
        Long userID = getUserId();
        if (userID == null) {
            response.setRedirect("/login");
            return null;
        }

        Template.NameBtn nameBtn = new Template.NameBtn(userDao.findById(userID).getNickname());
        Template.LogoutBtn logoutBtn = new Template.LogoutBtn();
        Template.UserBtn userBtn = new Template.UserBtn(nameBtn, logoutBtn);

        Template.UserLi[] userLis = userDao.findAll().stream()
                .map(user -> new Template.UserLi(user.getNickname()))
                .toArray(Template.UserLi[]::new);

        Template.UserList userList = new Template.UserList(userBtn, userLis);
        
        return userList;
    }

    private final EndPoint writePage = EndPoint.of(HttpMethod.GET, "/write.html");
    private Object writePageTemplate(HttpRequest request, HttpResponse response) {
        Long userID = getUserId();
        if (userID == null) {
            response.setRedirect("/login");
            return null;
        }

        response.setHeader("Content-Type", "text/html");
        String userButtons = getUserButtons(userDao.findById(userID).getNickname());
        String template = templateLoader.loadTemplate("/write.html", userButtons);
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
