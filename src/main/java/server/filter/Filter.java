package server.filter;

import server.http11.HttpRequest;
import server.http11.HttpResponse;

public interface Filter {
    void before(HttpRequest request, HttpResponse response);

    void after(HttpRequest request, HttpResponse response);
}
