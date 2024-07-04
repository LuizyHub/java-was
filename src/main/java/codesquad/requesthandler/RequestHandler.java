package codesquad.requesthandler;

import codesquad.http11.HttpMethod;
import codesquad.http11.HttpRequest;
import codesquad.http11.HttpResponse;

import java.io.IOException;

public interface RequestHandler {

    boolean canHandle(HttpMethod method, String path);

    HttpResponse handle(HttpRequest request) throws Exception;
}
