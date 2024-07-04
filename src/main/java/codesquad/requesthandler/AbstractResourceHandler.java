package codesquad.requesthandler;

import codesquad.http11.*;
import org.slf4j.Logger;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public abstract class AbstractResourceHandler implements RequestHandler {
    protected static final String STATIC_RESOURCE_PATH = "/static";
    protected final Logger log;

    protected AbstractResourceHandler(Logger log) {
        this.log = log;
    }

    @Override
    public boolean canHandle(HttpMethod method, String path) {
        if (!HttpMethod.GET.equals(method)) {
            return false;
        }

        URL resourceUrl = getClass().getResource(STATIC_RESOURCE_PATH + getResourcePathSuffix(path));
        if (resourceUrl == null) {
            return false;
        }
        try {
            if (!new File(resourceUrl.toURI()).isFile()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        String path = request.uri().getPath();
        String resourcePath = STATIC_RESOURCE_PATH + getResourcePathSuffix(path);

        log.info("Resource path: {}", resourcePath);

        try (InputStream resourceStream = getClass().getResourceAsStream(resourcePath)) {
            if (resourceStream == null) {
                log.info("Resource not found: {}", resourcePath);
                return HttpResponse.create(HttpStatus.NOT_FOUND, Map.of("Content-Type", "text/plain"), "404 Not Found".getBytes());
            }

            byte[] resourceBytes = resourceStream.readAllBytes();
            MimeType mimeType = MimeType.findMimeTypeByFileName(resourcePath);

            HttpResponse httpResponse = HttpResponse.create(HttpStatus.OK, Map.of("Content-Type", mimeType.type, "Content-Length", Integer.toString(resourceBytes.length)), resourceBytes);
            return httpResponse;
        } catch (IOException e) {
            log.error("Error serving static resource", e);
            return HttpResponse.ServerError();
        }
    }

    protected abstract String getResourcePathSuffix(String path);
}
