package server.router;

import server.function.RouterFunction;
import server.util.EndPoint;
import server.function.PairAdder;

import java.util.HashMap;
import java.util.Map;

public abstract class Router {
    private final String basePath;
    private Map<EndPoint, RouterFunction> routerFunctionMap = new HashMap<>();
    private boolean isInit = false;

    public Router() {
        basePath = setBasePath();
    }

    protected String setBasePath() {
        return "";
    }

    public final String getBasePath() {
        return basePath;
    }

    protected void addRouterFunctions(PairAdder<EndPoint, RouterFunction> routerFunctionAdder) {}

    private void routerFunctionAdder(EndPoint endPoint, RouterFunction routerFunction) {
        EndPoint endPointWithBasePath = EndPoint.of(endPoint.method(), basePath + endPoint.path());
        if (routerFunctionMap.containsKey(endPointWithBasePath)) {
            throw new IllegalArgumentException("Already exist router function for " + endPoint);
        }
        routerFunctionMap.put(endPointWithBasePath, routerFunction);
    }

    public final Map<EndPoint, RouterFunction> getRouterFunctionMap() {
        return routerFunctionMap;
    }

    public Router init() {
        if (isInit) {
            return this;
        }
        isInit = true;
        addRouterFunctions(this::routerFunctionAdder);
        setFieldImmutable();
        return this;
    }

    private void setFieldImmutable() {
        routerFunctionMap = Map.copyOf(routerFunctionMap);
    }
}
