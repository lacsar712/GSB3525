package com.elderly.care.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/{userId}/{role}")
@Component
public class WebSocketServer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    // key: userId, value: Session
    private static final Map<Long, Session> sessionMap = new ConcurrentHashMap<>();
    private static final Map<Long, String> userRoleMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId, @PathParam("role") String role) {
        sessionMap.put(userId, session);
        userRoleMap.put(userId, role);
        log.info("WebSocket open: userId={}, role={}, current total: {}", userId, role, sessionMap.size());
    }

    @OnClose
    public void onClose(@PathParam("userId") Long userId) {
        sessionMap.remove(userId);
        userRoleMap.remove(userId);
        log.info("WebSocket close: userId={}, current total: {}", userId, sessionMap.size());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("WebSocket receive message: {}", message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket error", error);
    }

    /**
     * Send to specific user
     */
    public static void sendMessageToUser(Long userId, String message) {
        Session session = sessionMap.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("WebSocket send error", e);
            }
        }
    }

    /**
     * Send to all users of a specific role
     */
    public static void sendMessageToRole(String role, String message) {
        for (Map.Entry<Long, String> entry : userRoleMap.entrySet()) {
            if ("ALL".equalsIgnoreCase(role) || entry.getValue().equalsIgnoreCase(role)) {
                sendMessageToUser(entry.getKey(), message);
            }
        }
    }

    /**
     * Broadcast to all
     */
    public static void broadcastMessage(String message) {
        for (Session session : sessionMap.values()) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    log.error("WebSocket broadcast error", e);
                }
            }
        }
    }
}
