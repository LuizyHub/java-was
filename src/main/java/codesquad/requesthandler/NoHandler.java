package codesquad.requesthandler;

import server.http11.HttpRequest;
import server.http11.HttpResponse;
import server.util.EndPoint;

public class NoHandler implements RequestHandler{
    //singleton
    private static final NoHandler instance = new NoHandler();

    private NoHandler() {
    }

    public static NoHandler getInstance() {
        return instance;
    }


    @Override
    public boolean canHandle(EndPoint endPoint) {
        return true;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        return HttpResponse.BadRequest();
    }
}
