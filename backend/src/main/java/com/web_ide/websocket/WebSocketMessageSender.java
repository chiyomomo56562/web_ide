package com.web_ide.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketMessageSender {
    private static SimpMessagingTemplate messagingTemplate;

    public WebSocketMessageSender(SimpMessagingTemplate template) {
        messagingTemplate = template;
    }

    public static void sendMessage(String destination, String message) {
        messagingTemplate.convertAndSend(destination, message);
    }
}