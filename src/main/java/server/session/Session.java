package server.session;

import java.util.UUID;

public class Session {
    private final String sessionId;
    private Long userId;

    private Session(String sessionId) {
        this.sessionId = sessionId;
    }

    public static Session create() {
        String sessionId = UUID.randomUUID().toString();
        return new Session(sessionId);
    }

    public String getSessionId() {
        return sessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}