package server.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemorySessionRepository implements SessionRepository<String> {
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
