package codesquad.filter;

import server.filter.Filter;
import server.http11.HttpRequest;
import server.http11.HttpResponse;

public class ThreadLocalContextManager implements ContextManager, Filter {

    private final ThreadLocal<Context> threadLocal = new ThreadLocal<>();

    @Override
    public void before(HttpRequest request, HttpResponse response) {
        threadLocal.set(new Context(request, response));
    }

    @Override
    public void after(HttpRequest request, HttpResponse response) {
        threadLocal.remove();
    }

    @Override
    public Context getContext() {
        return threadLocal.get();
    }
}
