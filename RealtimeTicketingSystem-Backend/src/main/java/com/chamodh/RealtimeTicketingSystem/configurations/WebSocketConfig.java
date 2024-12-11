package com.chamodh.RealtimeTicketingSystem.configurations;

import com.chamodh.RealtimeTicketingSystem.services.WebSocketTicketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket

/**
 * The WebSocketConfig class configures websocket handlers for the application.
 * This class uses the WebSocketConfigurer interface to register websocket handlers and
 * enable support.
 */
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketTicketHandler ticketHandler;

    /**
     * Constructs a new websocket config instance with the specified websocketTicektHandler.
     * @param ticketHandler the WebSocketTicketHandler to handle the broadcast messages.
     */
    public WebSocketConfig(WebSocketTicketHandler ticketHandler) {
        this.ticketHandler = ticketHandler;
    }

    /**
     * Registers Websocket handlers by mapping them to specific URL paths and setting allowed
     * origins. This method is a part of WebsocketConfiguration interface and the class overrides
     * the method.
     * @param registry the WebsocketHandlerRegistry to register handlers.
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(ticketHandler, "/log").setAllowedOrigins("*");
    }
}
