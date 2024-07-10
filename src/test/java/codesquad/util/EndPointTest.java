package codesquad.util;

import server.http11.HttpMethod;
import org.junit.jupiter.api.Test;
import server.util.EndPoint;

import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;

class EndPointTest {

    @Test
    public void testExactMatch() {
        EndPoint endPoint = EndPoint.of(HttpMethod.GET, "/users/123");
        assertTrue(endPoint.matches("GET", "/users/123"));
    }

    @Test
    public void testPathVariableMatch() {
        EndPoint endPoint = EndPoint.of(HttpMethod.GET, "/users/{id}");
        assertTrue(endPoint.matches("GET", "/users/123"));
    }

    @Test
    public void testPathVariableExtraction() {
        EndPoint endPoint = EndPoint.of(HttpMethod.GET, "/users/{id}");
        Matcher matcher = endPoint.getMatcher("/users/123");
        assertTrue(matcher.matches());
        assertTrue(matcher.group(1).equals("123"));
    }

    @Test
    public void testNoMatchDifferentMethod() {
        EndPoint endPoint = EndPoint.of(HttpMethod.GET, "/users/{id}");
        assertFalse(endPoint.matches("POST", "/users/123"));
    }

    @Test
    public void testNoMatchDifferentPath() {
        EndPoint endPoint = EndPoint.of(HttpMethod.GET, "/users/{id}");
        assertFalse(endPoint.matches("GET", "/orders/123"));
    }

    @Test
    public void testMultiplePathVariables() {
        EndPoint endPoint = EndPoint.of(HttpMethod.GET, "/users/{userId}/orders/{orderId}");
        Matcher matcher = endPoint.getMatcher("/users/123/orders/456");
        assertTrue(matcher.matches());
        assertTrue(matcher.group(1).equals("123"));
        assertTrue(matcher.group(2).equals("456"));
    }

}