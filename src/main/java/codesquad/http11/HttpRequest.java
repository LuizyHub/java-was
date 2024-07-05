package codesquad.http11;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static codesquad.util.StringUtil.*;

public record HttpRequest(HttpMethod method, URI uri, Map<String, String> headers, String body, Map<String, List<String>> queryParams) {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    public static HttpRequest pharse(InputStream in) throws IOException, URISyntaxException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        /**
         * HTTP-message =   start-line
         *                  *( header-field CRLF )
         *                  CRLF
         *                  [ message-body ]
         */
        String startLine = br.readLine();
        log.debug("startLine: {}", startLine);

        /**
         * request-line   = method SP request-target SP HTTP-version CRLF
         */
        String[] startLineTokens = startLine.split(SP);
        HttpMethod method = HttpMethod.of(startLineTokens[0]);

        String requestTarget = startLineTokens[1];

        String httpVersion = startLineTokens[2];

        /**
         * header-field   = field-name ":" OWS field-value OWS
         *                  field-name     = token
         *                  field-value    = *( field-content / obs-fold )
         *                  field-content  = field-vchar [ 1*( SP / HTAB ) field-vchar ]
         *                  field-vchar    = VCHAR / obs-text
         *
         *                  obs-fold       = CRLF 1*( SP / HTAB )
         *
         *                  OWS            = *( SP / HTAB )
         *
         *    A recipient MAY combine multiple header fields with the same field
         *    name into one "field-name: field-value" pair, without changing the
         *    semantics of the message, by appending each subsequent field value to
         *    the combined field value in order, separated by a comma.  The order
         *    in which header fields with the same field name are received is
         *    therefore significant to the interpretation of the combined field
         *    value; a proxy MUST NOT change the order of these field values when
         *    forwarding a message.
         */
        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while (!(headerLine = br.readLine()).isBlank()) {
            String[] headerTokens = headerLine.split(":", 2);
            String fieldName = headerTokens[0];
            String fieldValue = headerTokens[1].trim();

            // by appending each subsequent field value to the combined field value in order, separated by a comma
            if (headers.containsKey(fieldName)) {
                fieldValue = headers.get(fieldName) + ", " + fieldValue;
            }
            headers.put(fieldName, fieldValue);
        }

        headers = Collections.unmodifiableMap(headers);

        URI uri = createURI(requestTarget, headers.get("Host"));

        // Parse query parameters
        Map<String, List<String>> queryParams = parseQueryParams(uri);

        // Read message-body based on Content-Length
        StringBuilder bodyBuilder = new StringBuilder();
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[contentLength];
            int bytesRead = br.read(buffer, 0, contentLength);
            if (bytesRead > 0) {
                bodyBuilder.append(buffer, 0, bytesRead);
            }
        }
        String body = bodyBuilder.toString();

        return new HttpRequest(method, uri, headers, body, queryParams);
    }

    private static URI createURI(String requestTarget, String hostHeader) throws URISyntaxException {
        log.info("requestTarget: {}", requestTarget);
        log.info("hostHeader: {}", hostHeader);

        String scheme = "http";
        int port = -1;
        if (requestTarget.startsWith("/")) {
            // origin-form
            return new URI(scheme + "://" + hostHeader + requestTarget);
        } else if (requestTarget.startsWith("http://") || requestTarget.startsWith("https://")) {
            // absolute-form
            return new URI(requestTarget);
        } else if (requestTarget.equals("*")) {
            // asterisk-form
            if (hostHeader != null && hostHeader.contains(":")) {
                String[] hostTokens = hostHeader.split(":");
                hostHeader = hostTokens[0];
                port = Integer.parseInt(hostTokens[1]);
            }
            return new URI(scheme, null, hostHeader, port, "/", null, null);
        } else {
            // authority-form (primarily used with CONNECT method)
            return new URI("http://" + requestTarget);
        }
    }

    private static Map<String, List<String>> parseQueryParams(URI uri) {
        Map<String, List<String>> queryParams = new HashMap<>();
        if (uri.getQuery() != null) {
            String[] pairs = uri.getQuery().split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                String key = pair.substring(0, idx);
                String value = pair.substring(idx + 1).trim();
                if (queryParams.containsKey(key)) {
                    queryParams.get(key).add(value);
                } else {
                    queryParams.put(key, new ArrayList<>(List.of(value)));
                }
            }
        }
        return Collections.unmodifiableMap(queryParams);
    }
}
