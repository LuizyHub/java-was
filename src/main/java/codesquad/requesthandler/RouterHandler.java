package codesquad.requesthandler;

import codesquad.http11.HttpMethod;
import codesquad.http11.HttpRequest;
import codesquad.http11.HttpResponse;
import codesquad.router.Router;
import codesquad.router.RouterFunction;
import codesquad.router.UserCreateRouter;
import codesquad.util.ClasspathScanner;
import codesquad.util.EndPoint;
import codesquad.util.jsonconverter.JsonConverter;
import codesquad.util.jsonconverter.JsonConverterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouterHandler implements RequestHandler {
    private static final RouterHandler instance = new RouterHandler();
    private RouterHandler() {}
    public static RouterHandler getInstance() {return instance;}

    private static final Logger log = LoggerFactory.getLogger(RouterHandler.class);

    private final Map<EndPoint, RouterFunction> routerMap = new HashMap<>();

    {
        enrollRouters();
    }

    @Override
    public boolean canHandle(HttpMethod method, String path) {
        return routerMap.containsKey(new EndPoint(method, path));
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws Exception {  
        RouterFunction routerFunction = routerMap.get(new EndPoint(request.method(), request.uri().getPath()));
        HttpResponse response = HttpResponse.create();
        Object body = routerFunction.route(request, response);

        if (JsonConverterFactory.canConvert(body)) {
            response.setHeader("Content-Type", "application/json");
            JsonConverter converter = JsonConverterFactory.getConverter(body);
            response.setBody(converter.convertToJsonBytes(body));
        }
        else if (body instanceof byte[]) {
            response.setBody((byte[]) body);
        }
        else {
            response.setBody(body.toString());
        }
        return response;
    }

    private void enrollRouters() {
        try {
            // 패키지를 스캔하여 모든 클래스를 찾음
            List<Class<?>> classes = ClasspathScanner.findAllClassesUsingClassLoader("codesquad.router");
            for (Class<?> clazz : classes) {
                // Router 클래스를 상속받았는지 확인
                if (Router.class.isAssignableFrom(clazz) && !clazz.equals(Router.class)) {
                    try {
                        // getInstance() 메서드를 호출하여 인스턴스를 생성
                        Method getInstanceMethod = clazz.getMethod("getInstance");
                        Router routerInstance = (Router) getInstanceMethod.invoke(null);
                        setRouterHandlers(routerInstance);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        log.error("Failed to instantiate: " + clazz.getName());
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setRouterHandlers(Router router) {
        String path = router.getPath();

        if (router.get() != null) {
            routerMap.put(EndPoint.of(HttpMethod.GET, path), router.get());
        }
        if (router.post() != null) {
            routerMap.put(EndPoint.of(HttpMethod.POST, path), router.post());
        }
    }
}
