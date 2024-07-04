package codesquad.router;

import codesquad.http11.HttpStatus;
import codesquad.http11.MimeType;

import java.io.IOException;
import java.io.InputStream;

public class RegisterRouter extends Router {
    private static final RegisterRouter instance = new RegisterRouter();
    private RegisterRouter() {}
    public static RegisterRouter getInstance() {return instance;}

    {
        path = "/register.html";

        get = (request, response) -> {
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
        };
    }
}
