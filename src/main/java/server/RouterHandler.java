package server;

import codesquad.requesthandler.RequestHandler;
import server.http11.HttpRequest;
import server.http11.HttpResponse;
import server.function.RouterFunction;
import server.util.EndPoint;
import server.util.jsonconverter.JsonConverter;
import server.util.jsonconverter.JsonConverterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Map;

public class RouterHandler implements RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(RouterHandler.class);

    private final Map<EndPoint, RouterFunction> routerMap;

    public RouterHandler(Map<EndPoint, RouterFunction> routerMap) {
        this.routerMap = routerMap;
    }

    @Override
    public boolean canHandle(EndPoint endPoint) {
        return routerMap.containsKey(endPoint);
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws Exception {  
        RouterFunction routerFunction = routerMap.get(request.endPoint());

        HttpResponse response = HttpResponse.create();
        Object body = routerFunction.route(request, response);

        response.setBody(convertBody(body));
        return response;
    }

    private byte[] convertBody(Object body) throws Exception {
        if (JsonConverterFactory.canConvert(body)) {
            JsonConverter converter = JsonConverterFactory.getConverter(body);
            return converter.convertToJsonBytes(body);
        }
        else if (body instanceof byte[]) {
            return (byte[]) body;
        }
        else {
            return body.toString().getBytes();
        }
    }
}
