package codesquad.requesthandler;

import server.http11.HttpMethod;
import server.http11.HttpRequest;
import server.http11.HttpResponse;
import server.http11.MimeType;
import server.util.EndPoint;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UploadImageHandler implements RequestHandler {

    @Override
    public boolean canHandle(EndPoint endPoint) {
        return (endPoint.method() == HttpMethod.GET && endPoint.path().startsWith("/upload"));
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws Exception {
        String dir = getResourcePathSuffix(request.endPoint().path());
        Path path = Paths.get(dir);
        byte[] bytes = Files.readAllBytes(path);
        MimeType mimeType = MimeType.findMimeTypeByFileName(dir);

        response.setHeader("Content-Type", mimeType.type);
        response.setHeader("Content-Length", Integer.toString(bytes.length));
        response.setBody(bytes);
    }

    protected String getResourcePathSuffix(String path) {
        return getApplicationDirectory() + "/webapp" +path;
    }

    private String getApplicationDirectory() {
        // Get the path of the running JAR file or class
        String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        File jarFile = new File(path);
        // Get the directory
        return jarFile.getParentFile().getAbsolutePath();
    }
}
