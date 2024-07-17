package codesquad;

import server.config.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class Client {
    private final Configuration configuration;
    private final String IP;


    public Client(Configuration configuration) {
        this.configuration = configuration;
        this.IP = "http://localhost:" + configuration.getPort();
    }

    public Response sendRequest(String httpMethod, String path) throws IOException {
        return this.sendRequest(httpMethod, path, null, null);
    }

    public Response sendRequest(String httpMethod, String path, Map<String, String> headers) throws IOException {
        return this.sendRequest(httpMethod, path, headers, null);
    }

    public Response sendRequest(String httpMethod, String path, Map<String, String> headers, String Body) throws IOException {
        // 요청할 URL 설정
        URL url = new URL(IP + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod(httpMethod);

        // 요청 헤더 설정
        if (headers != null){
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        // 요청 바디 설정
        if (Body != null) {
            conn.setDoOutput(true);
            conn.getOutputStream().write(Body.getBytes());
        }


        int responseCode = conn.getResponseCode();
        String responseMessage = conn.getResponseMessage();
        Map<String, List<String>> headerFields = conn.getHeaderFields();

        int contentLength = conn.getContentLength();

        // 응답 바디 읽기
        String responseBody = null;

        try (InputStream inputStream = conn.getInputStream()) {
            if (contentLength > 0) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder bodyBuilder = new StringBuilder();
                char[] buffer = new char[contentLength];
                int bytesRead = br.read(buffer, 0, contentLength);
                if (bytesRead > 0) {
                    bodyBuilder.append(buffer, 0, bytesRead);
                }
                responseBody = bodyBuilder.toString();
            }
        }
        catch (IOException e) {
            // 응답 바디가 없는 경우
        }

        return new Response(responseCode, responseMessage, headerFields, responseBody);
    }

}
