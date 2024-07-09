package server.util;

import server.http11.HttpMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record EndPoint(HttpMethod method, String path) {
    private static final String PATH_VARIABLE_REGEX = "\\{[^/]+}";

    public static EndPoint of(HttpMethod method, String path) {
        return new EndPoint(method, path);
    }
    public static EndPoint of(HttpMethod method) {
        return new EndPoint(method, "");
    }

    public boolean matches(String method, String path) {
        if (!this.method.name().equalsIgnoreCase(method)) {
            return false;
        }
        Pattern pattern = Pattern.compile(convertPathToRegex(this.path));
        Matcher matcher = pattern.matcher(path);
        return matcher.matches();
    }

    public Matcher getMatcher(String path) {
        Pattern pattern = Pattern.compile(convertPathToRegex(this.path));
        return pattern.matcher(path);
    }

    private static String convertPathToRegex(String path) {
        return path.replaceAll(PATH_VARIABLE_REGEX, "([^/]+)");
    }

    public EndPoint addBasePath(String basePath) {
        return new EndPoint(method, basePath + path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EndPoint endPoint = (EndPoint) o;
        return method.equals(endPoint.method) && path.equals(endPoint.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }
}