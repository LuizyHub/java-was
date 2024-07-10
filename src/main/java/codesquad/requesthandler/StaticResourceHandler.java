package codesquad.requesthandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceHandler extends AbstractResourceHandler {
    private static final Logger log = LoggerFactory.getLogger(StaticResourceHandler.class);

    public StaticResourceHandler() {
        super(log);
    }

    @Override
    protected String getResourcePathSuffix(String path) {
        return path;
    }
}
