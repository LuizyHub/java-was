package codesquad;

import codesquad.dao.user.UserDao;
import codesquad.router.UsersRouter;
import server.http11.HttpMethod;
import codesquad.requesthandler.IndexPageHandler;
import codesquad.requesthandler.RequestHandler;
import codesquad.requesthandler.StaticResourceHandler;
import codesquad.router.RegisterRouter;
import server.function.RouterFunction;
import server.util.EndPoint;
import server.config.Configuration;
import server.function.Adder;
import server.function.PairAdder;
import server.router.Router;

public class ServerConfiguration extends Configuration {
    
    @Override
    protected void addRequestHandlers(Adder<RequestHandler> requestHandlerAdder) {
        requestHandlerAdder.add(new StaticResourceHandler());
        requestHandlerAdder.add(new IndexPageHandler());
    }

    @Override
    protected void addRouterFunctions(PairAdder<EndPoint, RouterFunction> routerFunctionAdder) {
        routerFunctionAdder.add(EndPoint.of(HttpMethod.GET, "/luizy"), (request, response) -> "Hello, Luizy!");
    }

    @Override
    protected void addRouters(Adder<Router> routerAdder) {
        routerAdder.add(new UsersRouter(UserDao.getInstance()));
        routerAdder.add(new RegisterRouter());
    }
}
