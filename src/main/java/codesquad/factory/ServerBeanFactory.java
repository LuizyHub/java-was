package codesquad.factory;

import codesquad.ServerConfiguration;
import codesquad.dao.user.UserDao;
import codesquad.requesthandler.IndexPageHandler;
import codesquad.requesthandler.StaticResourceHandler;
import codesquad.router.RegisterRouter;
import codesquad.router.UsersRouter;
import server.Server;
import server.config.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ServerBeanFactory {
    private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();

    public Server server() {
        return getOrComputeBean(Server.class, () -> new Server(configuration()));
    }

    public Configuration configuration() {
        return getOrComputeBean(Configuration.class, () -> new ServerConfiguration(this).init());
    }

    public StaticResourceHandler staticResourceHandler() {
        return getOrComputeBean(StaticResourceHandler.class, StaticResourceHandler::new);
    }

    public IndexPageHandler indexPageHandler() {
        return getOrComputeBean(IndexPageHandler.class, IndexPageHandler::new);
    }

    public UsersRouter usersRouter() {
        return getOrComputeBean(UsersRouter.class, () -> (UsersRouter) new UsersRouter(userDao()).init());
    }

    public RegisterRouter registerRouter() {
        return getOrComputeBean(RegisterRouter.class, () -> (RegisterRouter) new RegisterRouter().init());
    }

    public UserDao userDao() {
        return getOrComputeBean(UserDao.class, UserDao::new);
    }

    protected <T> T getOrComputeBean(Class<T> beanClass, Supplier<T> supplier) {
        return beanClass.cast(beanMap.computeIfAbsent(beanClass, k -> supplier.get()));
    }
}
