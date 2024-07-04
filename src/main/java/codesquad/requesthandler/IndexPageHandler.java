package codesquad.requesthandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexPageHandler extends AbstractResourceHandler {
    private static final Logger log = LoggerFactory.getLogger(IndexPageHandler.class);
    private static final IndexPageHandler instance = new IndexPageHandler();
    private static final String INDEX_PAGE = "/index.html";

    private IndexPageHandler() {
        super(log);
    }

    public static IndexPageHandler getInstance() {
        return instance;
    }

    @Override
    protected String getResourcePathSuffix(String path) {
        return path + INDEX_PAGE;
    }
}
