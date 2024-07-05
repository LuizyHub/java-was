package codesquad.router;

public abstract class Router {
    protected String path = null;
    protected RouterFunction get = null, post = null;

    public String getPath() {
        return path;
    }

    public RouterFunction get() {
        return get;
    }

    public RouterFunction post() {
        return post;
    }
}