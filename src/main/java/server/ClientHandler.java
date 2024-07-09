package server;

import codesquad.requesthandler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.config.Configuration;
import server.http11.HttpMethod;
import server.http11.HttpRequest;
import server.http11.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;
    private final List<RequestHandler> requestHandlers;

    public ClientHandler(Socket clientSocket, List<RequestHandler> requestHandlers) {
        this.clientSocket = clientSocket;
        this.requestHandlers = requestHandlers;
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
        for (RequestHandler requestHandler : requestHandlers) {
            if (requestHandler.canHandle(request.endPoint())) {
                return requestHandler;
            }
        }
        return NoHandler.getInstance();
    }
}
