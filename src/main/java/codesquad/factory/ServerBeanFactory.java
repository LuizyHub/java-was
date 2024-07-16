package codesquad.factory;

import codesquad.ServerConfiguration;
import codesquad.board.BoardDao;
import codesquad.board.H2BoardDao;
import codesquad.router.BoardRouter;
import codesquad.user.H2UserDao;
import codesquad.user.MemoryUserDao;
import codesquad.user.UserDao;
import codesquad.filter.ContextManager;
import codesquad.filter.ThreadLocalContextManager;
import codesquad.requesthandler.IndexPageHandler;
import codesquad.requesthandler.StaticResourceHandler;
import codesquad.router.RegisterRouter;
import codesquad.router.TemplateRouter;
import codesquad.router.UsersRouter;
import codesquad.template.TemplateLoader;
import org.h2.jdbcx.JdbcConnectionPool;
import server.Server;
import server.config.Configuration;
import codesquad.session.MemorySessionRepository;
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
        return getOrComputeBean(UserDao.class, () -> new H2UserDao(h2connectionPool()));
    }

    private JdbcConnectionPool h2connectionPool() {
        return getOrComputeBean(JdbcConnectionPool.class, () -> JdbcConnectionPool.create("jdbc:h2:./data/testdb", "sa", ""));
    }

    public SessionManager sessionManager() {
        return getOrComputeBean(SessionManager.class, () -> new SessionManager(contextManager(), sessionRepository()));
    }

    public SessionRepository sessionRepository() {
        return getOrComputeBean(SessionRepository.class, MemorySessionRepository::new);
    }

    public TemplateRouter templateRouter() {
        return getOrComputeBean(TemplateRouter.class, () -> (TemplateRouter) new TemplateRouter(templateLoader(), sessionManager(), userDao()).init());
    }

    public TemplateLoader templateLoader() {
        return getOrComputeBean(TemplateLoader.class, TemplateLoader::new);
    }

    public ContextManager contextManager() {
        return getOrComputeBean(ContextManager.class, this::threadLocalManager);
    }

    public ThreadLocalContextManager threadLocalManager() {
        return getOrComputeBean(ThreadLocalContextManager.class, ThreadLocalContextManager::new);
    }

    public BoardRouter boardRouter() {
        return getOrComputeBean(BoardRouter.class, () -> (BoardRouter) new BoardRouter(boardDao(), sessionManager()).init());
    }

    public BoardDao boardDao() {
        return getOrComputeBean(BoardDao.class, () -> new H2BoardDao(h2connectionPool()));
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
