package codesquad;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;
import server.config.Configuration;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegrationTest {

    private static final Configuration configuration = new TestServerConfiguration();
    static class TestServerConfiguration extends ServerConfiguration {
        @Override
        protected int setPort() {
            try (ServerSocket socket = new ServerSocket(0)) {
                return socket.getLocalPort();
            } catch (IOException e) {
                throw new RuntimeException("Failed to find an available port", e);
            }
        }
    }

    private static final Client client = new Client(configuration);

    @BeforeAll
    public static void setUp() throws InterruptedException {
        new Thread(() -> {
            try {
                Server server = new Server(configuration);
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(1000);
    }

    @Test void testClientResponse() throws IOException {
        Response response = client.sendRequest("GET", "/luizy", null, null);

        assertEquals(200, response.responseCode());
        assertEquals("OK", response.responseMessage());
        assertEquals("Hello, Luizy!", response.responseBody());
    }
}
