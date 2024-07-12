package server.session;

public interface SessionRepository<K> {
    void save(Session session);
    Session findById(K id);
    void deleteById(K id);
}
