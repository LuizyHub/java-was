package codesquad.requesthandler;

import org.slf4j.Logger;
import server.http11.*;
import server.util.EndPoint;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public abstract class AbstractResourceHandler implements RequestHandler {
    protected static final String STATIC_RESOURCE_PATH = "/static";
    protected final Logger log;

    protected AbstractResourceHandler(Logger log) {
        this.log = log;
    }

    @Override
    public boolean canHandle(EndPoint endPoint) {
        if (!HttpMethod.GET.equals(endPoint.method())) {
            return false;
        }

        String name = STATIC_RESOURCE_PATH + getResourcePathSuffix(endPoint.path());
        URL resourceUrl = getClass().getResource(name);
        if (resourceUrl == null) {
            return false;
        }
        if (!name.contains(".")) {
            return false;
        }
        return true;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        String path = request.uri().getPath();
        String resourcePath = STATIC_RESOURCE_PATH + getResourcePathSuffix(path);

        log.info("Resource path: {}", resourcePath);

        try (InputStream resourceStream = getClass().getResourceAsStream(resourcePath)) {
            if (resourceStream == null) {
                log.info("Resource not found: {}", resourcePath);
                response.setStatus(HttpStatus.NOT_FOUND);
            }

            byte[] resourceBytes = resourceStream.readAllBytes();
            MimeType mimeType = MimeType.findMimeTypeByFileName(resourcePath);

            response.setHeader("Content-Type", mimeType.type);
            response.setHeader("Content-Length", Integer.toString(resourceBytes.length));
            response.setBody(resourceBytes);
        } catch (IOException e) {
            log.error("Error serving static resource", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    protected abstract String getResourcePathSuffix(String path);
}
