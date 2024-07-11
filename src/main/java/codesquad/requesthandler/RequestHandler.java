package codesquad.requesthandler;

import server.http11.HttpRequest;
import server.http11.HttpResponse;
import server.util.EndPoint;

public interface RequestHandler {

    boolean canHandle(EndPoint endPoint);

    void handle(HttpRequest request, HttpResponse response) throws Exception;
}
