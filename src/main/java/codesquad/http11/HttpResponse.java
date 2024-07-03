package codesquad.http11;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static codesquad.util.StringUtil.*;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private final HttpStatus status;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpResponse(HttpStatus status, Map<String, String> headers, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse create(HttpStatus status, Map<String, String> headers, byte[] body) {
        return new HttpResponse(status, headers, body);
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
}
