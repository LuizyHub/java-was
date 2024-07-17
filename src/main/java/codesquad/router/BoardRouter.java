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
import server.util.FilePart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

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

        Map<String, FilePart> files = request.files();

        FilePart image = files.get("image");


        String title = new String(files.get("title").data());
        String content = new String(files.get("content").data());

        log.debug("title: {}, contents: {}", title, content);

        Board board = new Board(userId, title, content, image.fileName());
        board = boardDao.save(board);

        log.debug("filename: {}", image.fileName());

        log.debug("image: {}", image);
        try {
            saveBytesToFile(image.data(), board.getId() + image.fileName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        response.setRedirect("/index.html");

        return null;
    }

    private String getApplicationDirectory() {
        // Get the path of the running JAR file or class
        String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        File jarFile = new File(path);
        // Get the directory
        return jarFile.getParentFile().getAbsolutePath();
    }

    private void saveBytesToFile(byte[] bytes, String fileName) throws IOException {
        String dir = getApplicationDirectory() + "/webapp/upload";
        File directory = new File(dir);
        log.debug("dir: {}", dir);

        // Ensure the directory exists
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes);
        }
    }

}
