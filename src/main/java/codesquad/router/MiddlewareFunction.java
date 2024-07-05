package codesquad.router;

import codesquad.http11.HttpRequest;
import codesquad.http11.HttpResponse;

public interface MiddlewareFunction {
    boolean route(HttpRequest request, HttpResponse response);
}
