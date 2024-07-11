package codesquad.factory;

import codesquad.ServerConfiguration;
import codesquad.dao.user.UserDao;
import codesquad.requesthandler.IndexPageHandler;
import codesquad.requesthandler.StaticResourceHandler;
import codesquad.router.RegisterRouter;
import codesquad.router.UsersRouter;
import server.Server;
import server.config.Configuration;
import server.session.MemorySessionRepository;
import server.session.SessionManager;
import server.session.SessionRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ServerBeanFactory {
    private final Map<Class<?>, Object> beanMap = new HashMap<>();

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
        return getOrComputeBean(UsersRouter.class, () -> (UsersRouter) new UsersRouter(sessionManager(), userDao()).init());
    }

    public RegisterRouter registerRouter() {
        return getOrComputeBean(RegisterRouter.class, () -> (RegisterRouter) new RegisterRouter().init());
    }

    public UserDao userDao() {
        return getOrComputeBean(UserDao.class, UserDao::new);
    }

    public SessionManager sessionManager() {
        return getOrComputeBean(SessionManager.class, () -> new SessionManager(sessionRepository()));
    }

    public SessionRepository sessionRepository() {
        return getOrComputeBean(SessionRepository.class, MemorySessionRepository::new);
    }

    protected synchronized <T> T getOrComputeBean(Class<T> beanClass, Supplier<T> supplier) {
        Object o = beanMap.get(beanClass);
        if (o == null) {
            T t = supplier.get();
            beanMap.put(beanClass, t);
            o = t;
        }
        return beanClass.cast(o);
    }
}
