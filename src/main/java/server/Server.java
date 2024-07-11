package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.config.Configuration;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private final Configuration configuration;
    private final ExecutorService executorService;
    private final int port;

    public Server(Configuration configuration) {
        this.configuration = configuration;
        this.executorService = Executors.newFixedThreadPool(configuration.getThreadCount());
        this.port = configuration.getPort();
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port); // 8080 포트에서 서버를 엽니다.
        log.info("Listening for connection on port {} ....", port);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            log.info("Client connected: {}", clientSocket.getRemoteSocketAddress());
            executorService.execute(new ClientHandler(clientSocket, configuration.getRequestHandlers(), configuration.getFilters()));
        }
    }
}
