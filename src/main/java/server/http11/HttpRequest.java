package server.http11;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.util.ByteUtil;
import server.util.EndPoint;
import server.util.FilePart;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.*;

import static server.util.StringUtil.*;

public record HttpRequest(
        HttpMethod method,
        URI uri,
        Map<String, String> headers,
        String body,
        Map<String, List<String>> queryParams,
        EndPoint endPoint,
        Map<String, FilePart> files
        ) {
    // TODO: refactor to use HttpRequestBuilder
    public String getCookie(String key) {
        if (headers.containsKey("Cookie")) {
            String[] cookies = headers.get("Cookie").split(";");
            for (String cookie : cookies) {
                String[] cookieTokens = cookie.split("=", 2);
                if (cookieTokens[0].trim().equals(key)) {
                    return cookieTokens[1].trim();
                }
            }
        }
        return null;
    }

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    public static HttpRequest parse(InputStream in) throws IOException, URISyntaxException {

        /**
         * HTTP-message =   start-line
         *                  *( header-field CRLF )
         *                  CRLF
         *                  [ message-body ]
         */
        String startLine = new String(ByteUtil.readUntil(in, CRLF.getBytes()));
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
        while (!(headerLine = new String(ByteUtil.readUntil(in, CRLF.getBytes()))).isBlank()) {
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
        
        // Read message-body based on Content-Length
        StringBuilder bodyBuilder = new StringBuilder();
        byte[] bodyBytes = new byte[0];
        String body = "";
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            bodyBytes = new byte[contentLength];
            int bytesRead = in.read(bodyBytes, 0, contentLength);
            if (bytesRead < contentLength) {
                while (bytesRead < contentLength) {
                    bytesRead += in.read(bodyBytes, bytesRead, contentLength - bytesRead);
                }
            }
            body = new String(bodyBytes);
        }


        log.debug("body: {}", body);

        // Parse query parameters
        Map<String, List<String>> queryParams;
        if (headers.get("Content-Type") != null && headers.get("Content-Type").contains("application/x-www-form-urlencoded")) {
            queryParams = parseQueryParams(URLDecoder.decode(body, "UTF-8"));
        }
        else {
            queryParams = parseQueryParams(uri);
        }

        Map<String, FilePart> files = Map.of();

        if (headers.get("Content-Type") != null && headers.get("Content-Type").contains("multipart/form-data")) {
            files = new HashMap<>();
            String boundary = "--" + headers.get("Content-Type").split("boundary=")[1];
            byte[] boundaryBytes = boundary.getBytes();
            InputStream is = new ByteArrayInputStream(bodyBytes);

            byte[] partData;
            // Skip until first boundary
            ByteUtil.readUntil(is, boundaryBytes);
            while ((partData = ByteUtil.readUntil(is, boundaryBytes)).length != 0 ){
                // Parse partData
                InputStream partStream = new ByteArrayInputStream(partData);
                // Skip until first CRLF
                ByteUtil.readUntil(partStream, CRLF.getBytes());
                String partHeader = new String(ByteUtil.readUntil(partStream, CRLF.getBytes()));
                if (partHeader.contains("filename")) {
                    String name = partHeader.split("name=\"")[1].split("\"")[0];
                    String filename = partHeader.split("filename=\"")[1].split("\"")[0];
                    String ContentType = new String(ByteUtil.readUntil(partStream, CRLF.getBytes())).split(":")[1].trim();
                    partStream.read();
                    partStream.read();
                    byte[] fileData = partStream.readAllBytes();
                    files.put(name, new FilePart(name, filename, ContentType, fileData));
                } else {
                    String name = partHeader.split("name=\"")[1].split("\"")[0];
                    ByteUtil.readUntil(partStream, CRLF.getBytes());
                    byte[] fileData = partStream.readAllBytes();
                    files.put(name, new FilePart(name, null, null, fileData));
                }
            }
        }
        log.debug("files: {}", files);

        return new HttpRequest(method, uri, headers, body, queryParams, EndPoint.of(method, uri.getPath()), files);
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
        parseQueryParams(uri.getQuery(), queryParams);
        return Collections.unmodifiableMap(queryParams);
    }

    private static Map<String, List<String>> parseQueryParams(String string) {
        Map<String, List<String>> queryParams = new HashMap<>();
        parseQueryParams(string, queryParams);
        return Collections.unmodifiableMap(queryParams);
    }

    private static void parseQueryParams(String string, Map<String, List<String>> queryParams) {
        if (string != null) {
            String[] pairs = string.split("&");
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
    }
}
