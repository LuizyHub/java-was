package server.function;

import server.http11.HttpRequest;
import server.http11.HttpResponse;

@FunctionalInterface
public interface RouterFunction {

    /**
     * Route the request and return the response.
     * @param request: request to be routed
     * @param response: response to be modified (ex. setHeader, setStatus)
     * @return convertable response body
     */
    Object route(HttpRequest request, HttpResponse response);
}
