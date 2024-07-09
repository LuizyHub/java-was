package codesquad.router;

import server.function.RouterFunction;
import server.router.Router;
import server.util.EndPoint;
import server.function.PairAdder;
import server.http11.*;

import java.io.IOException;
import java.io.InputStream;

public class RegisterRouter extends Router {

    @Override
    protected String setBasePath() {
        return "/register.html";
    }

    @Override
    protected void addRouterFunctions(PairAdder<EndPoint, RouterFunction> routerFunctionAdder) {
        routerFunctionAdder.add(EndPoint.of(HttpMethod.GET, ""), this::getRegisterPage);
    }

    private Object getRegisterPage(HttpRequest request, HttpResponse response) {
        String resourcePath = "/static/registration/index.html";

        try (InputStream resourceStream = getClass().getResourceAsStream(resourcePath)) {
            if (resourceStream == null) {
                response.setStatus(HttpStatus.NOT_FOUND);
                response.setHeader("Content-Type", "text/plain");
                return "404 Not Found";
            }

            byte[] resourceBytes = resourceStream.readAllBytes();
            MimeType mimeType = MimeType.findMimeTypeByFileName(resourcePath);

            response.setStatus(HttpStatus.OK);
            response.setHeader("Content-Type", mimeType.type);
            response.setHeader("Content-Length", Integer.toString(resourceBytes.length));
            return resourceBytes;
        } catch (IOException e) {
            return null;
        }
    }
}
