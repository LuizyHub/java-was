package codesquad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int THREAD_COUNT = 10;
    private final Logger log = LoggerFactory.getLogger(Server.class);
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port); // 8080 포트에서 서버를 엽니다.
        log.info("Listening for connection on port {} ....", port);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            log.info("Client connected: {}", clientSocket.getRemoteSocketAddress());
            executorService.execute(new ClientHandler(clientSocket));

        }
    }
}
