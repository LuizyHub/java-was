package server.config;

import codesquad.requesthandler.NoHandler;
import codesquad.requesthandler.RequestHandler;
import server.RouterHandler;
import server.function.RouterFunction;
import server.util.EndPoint;
import server.function.Adder;
import server.function.PairAdder;
import server.router.Router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Configuration {
    private final int port;
    private final int threadCount;
    private List<RequestHandler> requestHandlers = new ArrayList<>();
    private Map<EndPoint, RouterFunction> routerFunctionMap = new HashMap<>();
    private boolean isInit = false;


    public Configuration() {
        this.port = setPort();
        this.threadCount = setThreadCount();
    }

    /**
     * @return port number
     */
    protected int setPort() {
        return 8080;
    }

    /**
     * @return port number
     * @default 8080
     */
    public final int getPort() {
        return port;
    }

    protected int setThreadCount() {
        return 10;
    }

    /**
     * @return thread count
     * @default 10
     */
    public final int getThreadCount() {
        return threadCount;
    }

    /**
     * Add request handlers
     * @param requestHandlerAdder
     */
    protected void addRequestHandlers(Adder<RequestHandler> requestHandlerAdder) {}

    private void requestHandlerAdder(RequestHandler requestHandler) {
        requestHandlers.add(requestHandler);
    };

    public final List<RequestHandler> getRequestHandlers() {
        return requestHandlers;
    }

    protected void addRouterFunctions(PairAdder<EndPoint, RouterFunction> routerFunctionAdder) {}

    private void routerFunctionAdder(EndPoint endPoint, RouterFunction routerFunction) {
        if (routerFunctionMap.containsKey(endPoint)) {
            throw new IllegalArgumentException("Already exist router function for " + endPoint);
        }
        routerFunctionMap.put(endPoint, routerFunction);
    };

    protected void addRouters(Adder<Router> routerAdder) {}

    private void routerAdder(Router router) {
        router.getRouterFunctionMap().forEach((endPoint, routerFunction) -> {
            if (routerFunctionMap.containsKey(endPoint)) {
                throw new IllegalArgumentException("Already exist router function for " + endPoint);
            }
            routerFunctionMap.put(endPoint, routerFunction);
        });
    };

    public final Configuration init() {
        if (isInit) {
            return this;
        }
        isInit = true;

        // setRouterFunctionMap
        addRouterFunctions(this::routerFunctionAdder);

        // setRouters
        addRouters(this::routerAdder);

        // setRouterFunctionMapImmutable
        routerFunctionMap = Map.copyOf(routerFunctionMap);

        // setRequestHandlers
        this.requestHandlers.add(new RouterHandler(routerFunctionMap));
        addRequestHandlers(this::requestHandlerAdder);
        this.requestHandlers.add(NoHandler.getInstance());
        requestHandlers = List.copyOf(requestHandlers);
        return this;
    }
}
