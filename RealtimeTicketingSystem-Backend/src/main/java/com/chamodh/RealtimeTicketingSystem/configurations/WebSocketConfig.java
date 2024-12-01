package com.chamodh.RealtimeTicketingSystem.configurations;

import com.chamodh.RealtimeTicketingSystem.services.WebSocketTicketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketTicketHandler ticketHandler;

    public WebSocketConfig(WebSocketTicketHandler ticketHandler) {
        this.ticketHandler = ticketHandler;
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(ticketHandler, "/log").setAllowedOrigins("*");
    }
}
