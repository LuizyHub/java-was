package server.session;

import codesquad.filter.ContextManager;
import server.filter.Filter;
import server.http11.HttpRequest;
import server.http11.HttpResponse;

import static codesquad.filter.ContextManager.*;

public class SessionManager implements Filter {
    private static final String SID = "SID";
    private final ContextManager contextManager;
    private final SessionRepository sessionRepository;

    public SessionManager(ContextManager contextManager, SessionRepository sessionRepository) {
        this.contextManager = contextManager;
        this.sessionRepository = sessionRepository;
    }

    /**
     * Get the session of the current thread.
     * default create session if not exist.
     * @return session
     */
    public Session getSession() {
        return getSession(true);
    }

    public Session getSession(boolean create) {
        Context context = contextManager.getContext();
        if (context == null) {
            throw new IllegalStateException("SessionManager is not initialized");
        }
        HttpRequest request = context.request();
        HttpResponse response = context.response();
        String sid = request.getCookie(SID);
        sid = (sid != null) ? sid : getSidFromResponse(response);
        if (sid == null) {
            if (!create) {
                return null;
            }
            Session session = Session.create();
            response.setCookie(SID, session.getSessionId());
            sessionRepository.save(session);
            return session;
        }

        Session session = sessionRepository.findById(sid);
        if (session == null) {
            if (!create) {
                return null;
            }
            session = Session.create();
            response.setCookie(SID, session.getSessionId());
            sessionRepository.save(session);
        }
        return session;
    }

    @Override
    public void before(HttpRequest request, HttpResponse response) { }

    @Override
    public void after(HttpRequest request, HttpResponse response) {
        Session session = getSession(false);
        if (session != null) {
            sessionRepository.save(session);
        }
    }

    private String getSidFromResponse(HttpResponse response) {
        String cookieHeader = response.getHeader("Set-Cookie");
        if (cookieHeader == null) {
            return null;
        }
        String[] cookies = cookieHeader.split(";");
        for (String cookie : cookies) {
            String[] keyValue = cookie.split("=");
            if (keyValue[0].trim().equals(SID)) {
                return keyValue[1];
            }
        }
        return null;
    }

}
