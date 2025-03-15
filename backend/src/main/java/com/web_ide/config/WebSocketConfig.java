package com.web_ide.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // ✅ SockJS 엔드포인트
                .setAllowedOriginPatterns("*") // CORS 설정
                .withSockJS(); // ✅ SockJS 사용!
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue"); // ✅ 메시지를 전달할 브로커 설정
        registry.setApplicationDestinationPrefixes("/app"); // ✅ 클라이언트에서 보낼 때 접두사 설정
    }
}