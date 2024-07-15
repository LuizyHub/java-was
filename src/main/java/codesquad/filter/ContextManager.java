package codesquad.filter;

import server.http11.HttpRequest;
import server.http11.HttpResponse;

public interface ContextManager {
    Context getContext();

    public static record Context(HttpRequest request, HttpResponse response) {
    }
}
