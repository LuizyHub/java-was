package codesquad.session;

import server.session.Session;
import server.session.SessionRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemorySessionRepository implements SessionRepository {
    private static final Map<String, Session> sessionStorage = new ConcurrentHashMap<>();

    @Override
    public void save(Session session) {
        sessionStorage.put(session.getSessionId(), session);
    }

    @Override
    public Session findById(String id) {
        return sessionStorage.get(id);
    }

    @Override
    public void deleteById(String id) {
        sessionStorage.remove(id);
    }
}
