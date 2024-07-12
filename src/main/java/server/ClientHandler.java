package server;

import codesquad.requesthandler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.config.Configuration;
import server.filter.Filter;
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
    private final List<Filter> filters;

    public ClientHandler(Socket clientSocket, List<RequestHandler> requestHandlers, List<Filter> filters) {
        this.clientSocket = clientSocket;
        this.requestHandlers = requestHandlers;
        this.filters = filters;
    }

    @Override
    public void run() {
        try (InputStream in = clientSocket.getInputStream();
             OutputStream out = clientSocket.getOutputStream()) {
            log.info("Client connected: {}", clientSocket.getRemoteSocketAddress());

            HttpRequest httpRequest = HttpRequest.pharse(in);
            HttpResponse httpResponse = HttpResponse.create();

            log.info("Request: {} {}", httpRequest.method(), httpRequest.uri());

            beforeHandler(httpRequest, httpResponse);

            RequestHandler requestHandler = getHandler(httpRequest);
            requestHandler.handle(httpRequest, httpResponse);

            afterHandler(httpRequest, httpResponse);

            httpResponse.write(out);

        } catch (Exception e) {
            log.error("Failed to accept client socket", e);
        }
    }

    private void beforeHandler(HttpRequest request, HttpResponse response) {
        for (Filter filter : filters) {
            filter.before(request, response);
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

    private void afterHandler(HttpRequest request, HttpResponse response) {
        // reverse order
        for (int i = filters.size() - 1; i >= 0; i--) {
            filters.get(i).after(request, response);
        }
    }

}
