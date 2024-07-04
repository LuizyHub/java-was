package codesquad.requesthandler;

import codesquad.http11.HttpMethod;
import codesquad.http11.HttpRequest;
import codesquad.http11.HttpResponse;

public class NoHandler implements RequestHandler{
    //singleton
    private static final NoHandler instance = new NoHandler();

    private NoHandler() {
    }

    public static NoHandler getInstance() {
        return instance;
    }


    @Override
    public boolean canHandle(HttpMethod method, String path) {
        return true;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        return HttpResponse.NotFound();
    }
}
