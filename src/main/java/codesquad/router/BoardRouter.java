package codesquad.router;

import codesquad.board.Board;
import codesquad.board.BoardDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.function.PairAdder;
import server.function.RouterFunction;
import server.http11.HttpMethod;
import server.http11.HttpRequest;
import server.http11.HttpResponse;
import server.router.Router;
import server.session.SessionManager;
import server.util.EndPoint;

import java.util.HashMap;
import java.util.Map;

import static server.util.StringUtil.*;

public class BoardRouter extends Router {
    private static final Logger log = LoggerFactory.getLogger(BoardRouter.class);
    private final BoardDao boardDao;
    private final SessionManager sessionManager;

    public BoardRouter(BoardDao boardDao, SessionManager sessionManager) {
        this.boardDao = boardDao;
        this.sessionManager = sessionManager;
    }

    @Override
    protected String setBasePath() {
        return "/boards";
    }

    @Override
    protected void addRouterFunctions(PairAdder<EndPoint, RouterFunction> routerFunctionAdder) {
        routerFunctionAdder.add(CREATE_BOARD, this::createBoard);
        routerFunctionAdder.add(EndPoint.of(HttpMethod.GET), (request, response) -> {
            response.setRedirect("/404.html");
            return null;
        });
    }

    private final EndPoint CREATE_BOARD = EndPoint.of(HttpMethod.POST);
    private Object createBoard(HttpRequest request, HttpResponse response) {
        Long userId = sessionManager.getSession().getUserId();
        if (userId == null) {
            response.setRedirect("/login");
            return null;
        }

        String contentType = request.headers().get("Content-Type");
        if (contentType == null || !contentType.startsWith("multipart/form-data")) {
            response.setRedirect("/404.html");
            return null;
        }

        String boundary = "--" + contentType.split("boundary=")[1];

        log.debug("boundary: {}", boundary);

        String[] split = request.body().split(boundary);
        Map<String, String> multipart = new HashMap<>();
        for (String s : split) {
            s = s.trim();
            String[] strings = s.split(CRLF+CRLF, 2);

            String startLine = strings[0];
            if (!startLine.contains("name=")) {
                continue;
            }
            String key = startLine.split("name=\"")[1].split("\"")[0];

            String body = strings[1].trim();

            log.debug("key: {}, body: {}", key, body);
            multipart.put(key, body);
        }

        String title = multipart.get("title");
        String content = multipart.get("content");

        log.debug("title: {}, contents: {}", title, content);

        Board board = new Board(userId, title, content, null);
        boardDao.save(board);

        response.setRedirect("/index.html");

        return null;
    }
}
