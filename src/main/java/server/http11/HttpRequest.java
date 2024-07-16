package server.http11;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.util.EndPoint;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static server.util.StringUtil.*;

public record HttpRequest(
        HttpMethod method,
        URI uri,
        Map<String, String> headers,
        String body,
        Map<String, List<String>> queryParams,
        Map<String, byte[]> files,
        EndPoint endPoint
) {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

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
    public static HttpRequest parse(InputStream in) throws IOException, URISyntaxException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.ISO_8859_1));

        String startLine = br.readLine();
        log.debug("startLine: {}", startLine);

        if (startLine == null) {
            throw new IOException("Invalid HTTP request: empty start line");
        }

        String[] startLineTokens = startLine.split(SP);
        if (startLineTokens.length != 3) {
            throw new IOException("Invalid HTTP request: malformed start line");
        }

        HttpMethod method = HttpMethod.of(startLineTokens[0]);
        String requestTarget = startLineTokens[1];
        String httpVersion = startLineTokens[2];

        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while ((headerLine = br.readLine()) != null && !headerLine.isEmpty()) {
            String[] headerTokens = headerLine.split(":", 2);
            if (headerTokens.length == 2) {
                String fieldName = headerTokens[0].trim();
                String fieldValue = headerTokens[1].trim();
                headers.merge(fieldName, fieldValue, (v1, v2) -> v1 + ", " + v2);
            }
        }

        log.debug("Headers parsed: {}", headers);

        URI uri = createURI(requestTarget, headers.get("Host"));

        byte[] bodyBytes = null;
        String body = "";
        Map<String, byte[]> files = new HashMap<>();
        Map<String, List<String>> queryParams = new HashMap<>();

        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            log.debug("Content-Length: {}", contentLength);
            bodyBytes = readAllBytes(in, contentLength);

            String contentType = headers.get("Content-Type");
            if (contentType != null && contentType.startsWith("multipart/form-data")) {
                log.debug("Processing multipart/form-data");
                parseMultipartFormData(bodyBytes, contentType, queryParams, files);
            } else if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
                log.debug("Processing application/x-www-form-urlencoded");
                body = new String(bodyBytes, StandardCharsets.UTF_8);
                queryParams = parseQueryParams(URLDecoder.decode(body, StandardCharsets.UTF_8));
            } else {
                log.debug("Processing other content type");
                body = new String(bodyBytes, StandardCharsets.UTF_8);
            }
        } else if ("POST".equals(method.name()) || "PUT".equals(method.name())) {
            log.debug("Reading body for POST/PUT request without Content-Length");
            bodyBytes = readAllBytes(in);
            body = new String(bodyBytes, StandardCharsets.UTF_8);
        }

        log.debug("Body length: {}", body.length());
        log.debug("Files count: {}", files.size());
        log.debug("Query params: {}", queryParams);

        if (queryParams.isEmpty()) {
            queryParams = parseQueryParams(uri);
        }

        return new HttpRequest(method, uri, headers, body, queryParams, files, EndPoint.of(method, uri.getPath()));
    }

    private static byte[] readAllBytes(InputStream in, int contentLength) throws IOException {
        byte[] buffer = new byte[contentLength];
        int bytesRead = 0;
        while (bytesRead < contentLength) {
            int chunk = in.read(buffer, bytesRead, contentLength - bytesRead);
            if (chunk == -1) {
                break;
            }
            bytesRead += chunk;
        }
        if (bytesRead < contentLength) {
            log.warn("Expected to read {} bytes, but only read {}", contentLength, bytesRead);
        }
        return Arrays.copyOf(buffer, bytesRead);
    }

    private static byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }

    private static URI createURI(String requestTarget, String hostHeader) throws URISyntaxException {
        log.info("requestTarget: {}", requestTarget);
        log.info("hostHeader: {}", hostHeader);

        String scheme = "http";
        int port = -1;
        if (requestTarget.startsWith("/")) {
            return new URI(scheme + "://" + hostHeader + requestTarget);
        } else if (requestTarget.startsWith("http://") || requestTarget.startsWith("https://")) {
            return new URI(requestTarget);
        } else if (requestTarget.equals("*")) {
            if (hostHeader != null && hostHeader.contains(":")) {
                String[] hostTokens = hostHeader.split(":");
                hostHeader = hostTokens[0];
                port = Integer.parseInt(hostTokens[1]);
            }
            return new URI(scheme, null, hostHeader, port, "/", null, null);
        } else {
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

    private static void parseMultipartFormData(byte[] bodyBytes, String contentType, Map<String, List<String>> queryParams, Map<String, byte[]> files) throws IOException {
        String boundary = "--" + contentType.split("boundary=")[1];
        log.debug("Multipart boundary: {}", boundary);

        ByteArrayInputStream bais = new ByteArrayInputStream(bodyBytes);
        byte[] boundaryBytes = ("\r\n" + boundary).getBytes(StandardCharsets.ISO_8859_1);
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Skip until first boundary
        boolean foundFirstBoundary = false;
        while ((bytesRead = bais.read(buffer)) != -1) {
            int boundaryIndex = indexOf(buffer, boundaryBytes, 0, bytesRead);
            if (boundaryIndex != -1) {
                foundFirstBoundary = true;
                bais.reset();
                bais.skip(boundaryIndex + boundaryBytes.length);
                break;
            }
            bais.mark(buffer.length);
        }

        if (!foundFirstBoundary) {
            log.warn("Could not find the first boundary in multipart data");
            return;
        }

        // Read parts
        while ((bytesRead = bais.read(buffer)) != -1) {
            int boundaryIndex = indexOf(buffer, boundaryBytes, 0, bytesRead);
            if (boundaryIndex != -1) {
                baos.write(buffer, 0, boundaryIndex);
                byte[] partBytes = baos.toByteArray();
                processPart(partBytes, queryParams, files);
                baos.reset();
                bais.reset();
                bais.skip(boundaryIndex + boundaryBytes.length);
            } else {
                baos.write(buffer, 0, bytesRead);
            }
            bais.mark(buffer.length);
        }

        log.debug("Finished parsing multipart data. Query params: {}, Files: {}", queryParams.size(), files.size());
    }
    private static void processPart(byte[] partBytes, Map<String, List<String>> queryParams, Map<String, byte[]> files) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(partBytes);
        BufferedReader br = new BufferedReader(new InputStreamReader(bais, StandardCharsets.ISO_8859_1));

        // Read headers
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(":", 2);
            headers.put(headerParts[0].trim(), headerParts[1].trim());
        }

        // Process Content-Disposition
        String contentDisposition = headers.get("Content-Disposition");
        String[] dispositionParts = contentDisposition.split(";");
        String name = null;
        String filename = null;
        for (String part : dispositionParts) {
            part = part.trim();
            if (part.startsWith("name=")) {
                name = part.substring(6, part.length() - 1);
            } else if (part.startsWith("filename=")) {
                filename = part.substring(10, part.length() - 1);
            }
        }

        // Read content
        byte[] content = bais.readAllBytes();

        if (filename != null) {
            // This is a file
            files.put(name, content);
        } else {
            // This is a form field
            String value = new String(content, StandardCharsets.UTF_8).trim();
            queryParams.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
        }
    }

    private static int indexOf(byte[] array, byte[] target, int start, int end) {
        outer:
        for (int i = start; i < end - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }
}