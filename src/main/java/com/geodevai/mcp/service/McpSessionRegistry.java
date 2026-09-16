package com.geodevai.mcp.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class McpSessionRegistry {

    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();

    public String createSession() {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, new SessionInfo(sessionId, true));
        return sessionId;
    }

    public String createSession(String sessionId) {
        sessions.put(sessionId, new SessionInfo(sessionId, true));
        return sessionId;
    }

    public boolean isActive(String sessionId) {
        SessionInfo info = sessions.get(sessionId);
        return info != null && info.isActive();
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public int sessionCount() {
        return sessions.size();
    }

    @RequiredArgsConstructor
    @Getter
    public static class SessionInfo {
        private final String sessionId;
        private final boolean active;
    }
}
