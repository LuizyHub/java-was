package server.function;

import server.http11.HttpRequest;
import server.http11.HttpResponse;

public interface MiddlewareFunction {
    boolean route(HttpRequest request, HttpResponse response);
}
