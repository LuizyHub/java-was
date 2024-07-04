package codesquad;

import codesquad.http11.*;
import codesquad.requesthandler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;
    private final List<RequestHandler> requestHandlers = List.of(
            RouterHandler.getInstance(),
            StaticResourceHandler.getInstance(),
            IndexPageHandler.getInstance()
    );

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

            RequestHandler requestHandler = getHandler(httpRequest);
            log.info("RequestHandler: {}", requestHandler);
            HttpResponse httpResponse = requestHandler.handle(httpRequest);
            httpResponse.write(out);

        } catch (Exception e) {
            log.error("Failed to accept client socket", e);
        }
    }

    private RequestHandler getHandler(HttpRequest request) {
        HttpMethod method = request.method();
        String path = request.uri().getPath();

        for (RequestHandler requestHandler : requestHandlers) {
            if (requestHandler.canHandle(method, path)) {
                return requestHandler;
            }
        }

        return NoHandler.getInstance();
    }
}
