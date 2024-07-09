package codesquad.requesthandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexPageHandler extends AbstractResourceHandler {
    private static final Logger log = LoggerFactory.getLogger(IndexPageHandler.class);
    private static final String INDEX_PAGE = "/index.html";

    public IndexPageHandler() {
        super(log);
    }

    @Override
    protected String getResourcePathSuffix(String path) {
        return path + INDEX_PAGE;
    }
}
