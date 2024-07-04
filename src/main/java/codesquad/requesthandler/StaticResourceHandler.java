package codesquad.requesthandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceHandler extends AbstractResourceHandler {
    private static final Logger log = LoggerFactory.getLogger(StaticResourceHandler.class);
    private static final StaticResourceHandler instance = new StaticResourceHandler();

    private StaticResourceHandler() {
        super(log);
    }

    public static StaticResourceHandler getInstance() {
        return instance;
    }

    @Override
    protected String getResourcePathSuffix(String path) {
        return path;
    }
}
