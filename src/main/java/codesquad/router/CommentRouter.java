package codesquad.router;

import codesquad.comment.Comment;
import codesquad.comment.CommentDao;
import server.function.PairAdder;
import server.function.RouterFunction;
import server.http11.HttpMethod;
import server.http11.HttpRequest;
import server.http11.HttpResponse;
import server.router.Router;
import server.session.SessionManager;
import server.util.EndPoint;

public class CommentRouter extends Router {
    private final CommentDao commentDao;
    private final SessionManager sessionManager;

    public CommentRouter(CommentDao commentDao, SessionManager sessionManager) {
        this.commentDao = commentDao;
        this.sessionManager = sessionManager;
    }

    @Override
    protected String setBasePath() {
        return "/comments";
    }

    @Override
    protected void addRouterFunctions(PairAdder<EndPoint, RouterFunction> routerFunctionAdder) {
        routerFunctionAdder.add(createComment, this::createComment);
    }

    private final EndPoint createComment = EndPoint.of(HttpMethod.POST);
    private Object createComment(HttpRequest request, HttpResponse response) {
        Long userId = sessionManager.getSession().getUserId();
        if (userId == null) {
            response.setRedirect("/login");
            return null;
        }

        Long boardId = Long.parseLong(request.queryParams().get("boardId").get(0));
        String content = request.queryParams().get("content").get(0);
        if (boardId == null) {
            response.setRedirect("/404.html");
            return null;
        }
        if (content == null) {
            response.setRedirect("/index.html");
            return null;
        }

        Comment comment = new Comment(userId, boardId, content);
        commentDao.save(comment);
        response.setRedirect("/index.html");

        return null;
    }
}
