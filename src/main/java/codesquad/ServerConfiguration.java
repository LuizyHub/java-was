package codesquad;

import codesquad.factory.ServerBeanFactory;
import codesquad.requesthandler.RequestHandler;
import server.config.Configuration;
import server.filter.Filter;
import server.function.Adder;
import server.router.Router;

public class ServerConfiguration extends Configuration {

    private final ServerBeanFactory factory;

    public ServerConfiguration(ServerBeanFactory serverBeanFactory) {
        this.factory = serverBeanFactory;
    }

    @Override
    protected void addFilters(Adder<Filter> filterAdder) {
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
    }
}
