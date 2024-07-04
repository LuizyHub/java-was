package codesquad.util;

import codesquad.http11.HttpMethod;

public record EndPoint(HttpMethod method, String path) {

    public static EndPoint of(HttpMethod method, String path) {
        return new EndPoint(method, path);
    }
}
