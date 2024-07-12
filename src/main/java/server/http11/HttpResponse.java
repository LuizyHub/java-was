package server.http11;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static server.util.StringUtil.*;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private HttpStatus status;
    private final Map<String, String> headers;
    private byte[] body;

    public HttpResponse(HttpStatus status, Map<String, String> headers, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }
    public static HttpResponse create() {
        return new HttpResponse(HttpStatus.OK, new HashMap<>(), null);
    }

    public static HttpResponse create(HttpStatus status, Map<String, String> headers, byte[] body) {
        return new HttpResponse(status, headers, body);
    }

    public static HttpResponse NotFound() {
        return new HttpResponse(HttpStatus.NOT_FOUND, Map.of(), null);
    }

    public static HttpResponse ServerError() {
        return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, Map.of(), null);
    }

    public static HttpResponse BadRequest() {
        return new HttpResponse(HttpStatus.BAD_REQUEST, Map.of(), null);
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setBody(String body) {
        this.body = body.getBytes();
    }

    public void setRedirect(String location) {
        setHeader("Location", location);
        setStatus(HttpStatus.FOUND);
    }

    public void setCookie(String key, String value) {
        setCookie(key, value, -2, "/", false, true);
    }

    public void setCookie(String key, String value, int maxAge, String path, boolean secure, boolean httpOnly) {
        StringBuilder sb = new StringBuilder();

        if (headers.containsKey("Set-Cookie")) {
            sb.append(headers.get("Set-Cookie")).append("; ");
        }

        sb.append(key).append("=").append(value).append("; ");
        if (maxAge != -2) { // -2: session cookie, -1: delete cookie (set Max-Age=0
            sb.append("Max-Age=").append(maxAge).append("; ");
        }
        sb.append("Path=").append(path).append("; ");
        if (secure) {
            sb.append("Secure; ");
        }
        if (httpOnly) {
            sb.append("HttpOnly; ");
        }

        setHeader("Set-Cookie", sb.toString());
    }
    public byte[] toRaw() {
        StringBuilder sb = new StringBuilder();
        // 상태 라인
        sb.append("HTTP/1.1").append(SP).append(status.code).append(SP).append(status.message).append(CRLF);

        // 헤더
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String headerName = entry.getKey();
            String headerValue = entry.getValue();
            sb.append(headerName).append(": ").append(headerValue).append(CRLF);
        }

        // 헤더와 본문을 구분하는 빈 줄
        sb.append(CRLF);

        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        // 본문
        if (body != null) {
            // 두 바이트 배열을 합친다
            byte[] raw = new byte[bytes.length + body.length];
            System.arraycopy(bytes, 0, raw, 0, bytes.length);
            System.arraycopy(body, 0, raw, bytes.length, body.length);
            bytes = raw;
        }

        return bytes;
    }

    public void write(OutputStream out) {
        try {
            out.write(toRaw());
            out.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public String getHeader(String s) {
        return headers.get(s);
    }
}
