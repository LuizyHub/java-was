package codesquad.router;

import codesquad.http11.HttpRequest;
import codesquad.http11.HttpResponse;

@FunctionalInterface
public interface RouterFunction {

    Object route(HttpRequest request, HttpResponse response);
}
