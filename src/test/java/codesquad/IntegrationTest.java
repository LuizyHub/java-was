package codesquad;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import server.Server;
import server.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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

    @Nested
    class step_1 {

        @Nested
        class 정적인_html {

            @Test
            void index_html을_응답한다() throws IOException {
                // when
                Response response = client.sendRequest("GET","/index.html");

                // then
                Map<String, List<String>> headerFields = response.headerFields();
                String contentType = headerFields.get("Content-Type").get(0);

                assertEquals("text/html", contentType);

                String responseBody = response.responseBody();
                String expected = getFileText("src/main/resources/static/index.html");
                System.out.println(expected);
                assertEquals(expected, responseBody);
            }
        }
    }

    @Nested
    class step_2_다양한_컨텐츠_타입_지원 {

        @Nested
        class 컨텐츠_타입 {

            @Test
            void html을_지원한다() throws IOException {
                // when
                Response response = client.sendRequest("GET","/index.html");

                // then
                Map<String, List<String>> headerFields = response.headerFields();
                String contentType = headerFields.get("Content-Type").get(0);

                assertEquals("text/html", contentType);

                String responseBody = response.responseBody();
                String expected = getFileText("src/main/resources/static/index.html");
                assertEquals(expected, responseBody);
            }

            @Test
            void css를_지원한다() throws IOException {
                // when
                Response response = client.sendRequest("GET","/main.css");

                // then
                Map<String, List<String>> headerFields = response.headerFields();
                String contentType = headerFields.get("Content-Type").get(0);

                assertEquals("text/css", contentType);

                String responseBody = response.responseBody();
                String expected = getFileText("src/main/resources/static/main.css");
                assertEquals(expected, responseBody);
            }

            @Test
            void js를_지원한다() throws IOException {
                // when
                Response response = client.sendRequest("GET","/main.js");

                // then
                Map<String, List<String>> headerFields = response.headerFields();
                String contentType = headerFields.get("Content-Type").get(0);

                assertEquals("application/javascript", contentType);

                String responseBody = response.responseBody();
                String expected = getFileText("src/main/resources/static/main.js");
                assertEquals(expected, responseBody);
            }

            @Test
            void icon을_지원한다() throws IOException {
                // when
                Response response = client.sendRequest("GET","/favicon.ico");

                // then
                Map<String, List<String>> headerFields = response.headerFields();
                String contentType = headerFields.get("Content-Type").get(0);

                assertEquals("image/x-icon", contentType);

                String responseBody = response.responseBody();
                String expected = getFileText("src/main/resources/static/favicon.ico");
                assertEquals(expected, responseBody);
            }

            @Test
            void png를_지원한다() throws IOException {
                // when
                Response response = client.sendRequest("GET","/mushroom.png");

                // then
                Map<String, List<String>> headerFields = response.headerFields();
                String contentType = headerFields.get("Content-Type").get(0);

                assertEquals("image/png", contentType);

                String responseBody = response.responseBody();
                String expected = getFileText("src/main/resources/static/mushroom.png");
                assertEquals(expected, responseBody);
            }

            @Test
            void jpg를_지원한다() throws IOException {
                // when
                Response response = client.sendRequest("GET","/mario.jpeg");

                // then
                Map<String, List<String>> headerFields = response.headerFields();
                String contentType = headerFields.get("Content-Type").get(0);

                assertEquals("image/jpeg", contentType);

                String responseBody = response.responseBody();
                String expected = getFileText("src/main/resources/static/mario.jpeg");
                assertEquals(expected, responseBody);
            }
        }
    }

    @Nested
    class step_4_POST로_회원가입 {

        @Nested
        class 회원가입은 {

            @Test
            void POST_요청으로하면_성공한다() throws IOException {
                // given
                String body = "userId=codingluizy&password=1234&name=박정제";
                Map<String, String> headers = Map.of(
                        "Content-Type", "application/x-www-form-urlencoded",
                        "Content-Length", String.valueOf(body.getBytes().length)
                );

                Response response = client.sendRequest("POST", "/user/create", headers, body);

                // then
                assertEquals(302, response.responseCode());
                assertEquals("Found", response.responseMessage());
                assertEquals("/index.html", response.headerFields().get("Location").get(0));
            }

            @Test
            void GET_요청으로하면_실패한다() throws IOException {
                // given
                Response response = client.sendRequest("GET", "/user/create?userId=codingluizy&password=1234&name=박정제");

                // then
                assertNotEquals(302, response.responseCode());
                assertNotEquals("Found", response.responseMessage());
            }

            @Test
            void 필수_파라미터가_없으면_실패한다() throws IOException {
                // given
                String body = "userId=codingluizy&password=1234";
                Map<String, String> headers = Map.of(
                        "Content-Type", "application/x-www-form-urlencoded",
                        "Content-Length", String.valueOf(body.getBytes().length)
                );

                Response response = client.sendRequest("POST", "/user/create", headers, body);

                // then
                assertEquals(400, response.responseCode());
                assertEquals("Bad Request", response.responseMessage());
            }
        }
    }

    private static String getFileText(String path) throws IOException {
        File file = new File(path);
        String expected = new String(file.toURI().toURL().openStream().readAllBytes());
        return expected;
    }

}
