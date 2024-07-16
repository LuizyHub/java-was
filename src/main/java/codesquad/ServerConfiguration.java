package codesquad;

import codesquad.factory.ServerBeanFactory;
import codesquad.requesthandler.RequestHandler;
import server.config.Configuration;
import server.filter.Filter;
import server.function.Adder;
import server.function.PairAdder;
import server.function.RouterFunction;
import server.http11.HttpMethod;
import server.router.Router;
import server.util.EndPoint;

public class ServerConfiguration extends Configuration {

    private final ServerBeanFactory factory;

    public ServerConfiguration(ServerBeanFactory serverBeanFactory) {
        this.factory = serverBeanFactory;
    }

    @Override
    protected void addFilters(Adder<Filter> filterAdder) {
        filterAdder.add(factory.threadLocalManager());
        filterAdder.add(factory.sessionManager());
    }

    @Override
    protected void addRequestHandlers(Adder<RequestHandler> requestHandlerAdder) {
        requestHandlerAdder.add(factory.staticResourceHandler());
        requestHandlerAdder.add(factory.indexPageHandler());
    }

    @Override
    protected void addRouters(Adder<Router> routerAdder) {
        routerAdder.add(factory.usersRouter());
        routerAdder.add(factory.registerRouter());
        routerAdder.add(factory.templateRouter());
        routerAdder.add(factory.boardRouter());
    }

    @Override
    protected void addRouterFunctions(PairAdder<EndPoint, RouterFunction> routerFunctionAdder) {
        routerFunctionAdder.add(EndPoint.of(HttpMethod.GET, "/logout"), (request, response) -> {
            response.setCookie("SID", "", -1, "/", false, true);
            response.setRedirect("/index.html");
            return null;
        });
        routerFunctionAdder.add(EndPoint.of(HttpMethod.GET, "/"), (request, response) -> {
            response.setRedirect("/index.html");
            return null;
        });
    }
}
