package codesquad.http11;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.http11.HttpMethod;
import server.http11.HttpRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void parse_validGetRequest_originForm() throws Exception {
        String rawRequest = "GET /index.html HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Content-Length: 0\r\n" +
                "\r\n";
        InputStream in = new ByteArrayInputStream(rawRequest.getBytes());

        HttpRequest request = HttpRequest.pharse(in);

        assertEquals(HttpMethod.GET, request.method());
        assertEquals(new URI("http://localhost/index.html"), request.uri());
        assertEquals(Map.of("Host", "localhost", "Content-Length", "0"), request.headers());
        assertEquals("", request.body());
    }

    @Test
    void parse_validGetRequest_absoluteForm() throws Exception {
        String rawRequest = "GET http://localhost:8080/index.html HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Content-Length: 0\r\n" +
                "\r\n";
        InputStream in = new ByteArrayInputStream(rawRequest.getBytes());

        HttpRequest request = HttpRequest.pharse(in);

        assertEquals(HttpMethod.GET, request.method());
        assertEquals(new URI("http://localhost:8080/index.html"), request.uri());
        assertEquals(Map.of("Host", "localhost:8080", "Content-Length", "0"), request.headers());
        assertEquals("", request.body());
    }

    @Test
    void parse_validPostRequest_withBody() throws Exception {
        String rawRequest = "POST /submit HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Content-Length: 11\r\n" +
                "\r\n" +
                "Hello World";
        InputStream in = new ByteArrayInputStream(rawRequest.getBytes());

        HttpRequest request = HttpRequest.pharse(in);

        assertEquals(HttpMethod.POST, request.method());
        assertEquals(new URI("http://localhost/submit"), request.uri());
        assertEquals(Map.of("Host", "localhost", "Content-Length", "11"), request.headers());
        assertEquals("Hello World", request.body());
    }

    @Test
    void parse_invalidRequest_missingMethod() {
        String rawRequest = "/index.html HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Content-Length: 0\r\n" +
                "\r\n";
        InputStream in = new ByteArrayInputStream(rawRequest.getBytes());

        assertThrows(IllegalArgumentException.class, () -> HttpRequest.pharse(in));
    }

    @Test
    void parse_invalidRequest_missingUri() {
        String rawRequest = "GET  HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Content-Length: 0\r\n" +
                "\r\n";
        InputStream in = new ByteArrayInputStream(rawRequest.getBytes());

        assertThrows(URISyntaxException.class, () -> HttpRequest.pharse(in));
    }

    @Test
    void parse_validRequest_asteriskForm() throws Exception {
        String rawRequest = "OPTIONS * HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Content-Length: 0\r\n" +
                "\r\n";
        InputStream in = new ByteArrayInputStream(rawRequest.getBytes());

        HttpRequest request = HttpRequest.pharse(in);

        assertEquals(HttpMethod.OPTIONS, request.method());
        assertEquals(new URI("http://localhost/"), request.uri());
        assertEquals(Map.of("Host", "localhost", "Content-Length", "0"), request.headers());
        assertEquals("", request.body());
    }

    @Test
    @DisplayName("요청 헤더에 중복된 필드가 있을 때, 모든 값을 콤마로 구분하여 하나의 필드 값으로 합친다.")
    void parse_validRequest_withDuplicatedHeaderFields() throws Exception {
        String rawRequest = "GET /index.html HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Accept: text/html\r\n" +
                "Accept: application/json\r\n" +
                "Content-Length: 0\r\n" +
                "\r\n";
        InputStream in = new ByteArrayInputStream(rawRequest.getBytes());

        HttpRequest request = HttpRequest.pharse(in);

        assertEquals(HttpMethod.GET, request.method());
        assertEquals(new URI("http://localhost/index.html"), request.uri());
        assertEquals(Map.of("Host", "localhost", "Accept", "text/html, application/json", "Content-Length", "0"), request.headers());
        assertEquals("", request.body());
    }

}