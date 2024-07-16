package server.session;

public interface SessionRepository {
    void save(Session session);
    Session findById(String id);
    void deleteById(String id);
}
