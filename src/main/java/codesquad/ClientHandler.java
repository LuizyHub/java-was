package codesquad;

import codesquad.http11.HttpRequest;
import codesquad.http11.HttpResponse;
import codesquad.http11.HttpStatus;
import codesquad.http11.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ClientHandler implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (InputStream in = clientSocket.getInputStream();
             OutputStream out = clientSocket.getOutputStream()) {
            log.info("Client connected: {}", clientSocket.getRemoteSocketAddress());

            HttpRequest httpRequest = HttpRequest.pharse(in);

            log.info("Request: {}", httpRequest.method());
            log.info("Request: {}", httpRequest.uri());
            log.info("Request: {}", httpRequest.headers());
            log.info("Request: {}", httpRequest.body());

            // path를 읽어서 /resources/static/{path} 파일을 읽어서 응답으로 보내줍니다.
            String path = httpRequest.uri().getPath();
            File file = new File("src/main/resources/static", path);

            if (file.exists() && file.isFile()) {
                // 파일을 읽어서 바이트 배열로 변환합니다.
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] fileBytes = new byte[(int) file.length()];
                fileInputStream.read(fileBytes);
                fileInputStream.close();

                // 파일의 확장자로 Content-Type을 설정합니다.
                MimeType mimeType = MimeType.findMimeTypeByFileName(file.getName());

                // HTTP 응답을 생성합니다.
                HttpResponse httpResponse = HttpResponse.create(HttpStatus.OK, Map.of("Content-Type", mimeType.type), fileBytes);
                httpResponse.write(out);
            } else {
                // 파일이 존재하지 않는 경우 404 응답
                HttpResponse httpResponse = HttpResponse.create(HttpStatus.NOT_FOUND, Map.of(), null);
                httpResponse.write(out);
            }

        } catch (Exception e) {
            log.error("Failed to accept client socket", e);
        }
    }
}
