package com.chamodh.RealtimeTicketingSystem.controllers;

import com.chamodh.RealtimeTicketingSystem.services.WebSocketTicketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin
/**
 * The WebsocketController class handles HTTP requests related to starting and
 * stopping the backend websocket.
 * It provides endpoints for starting and stopping the backend processes.
 */
public class WebsocketController {

    @Autowired
    private WebSocketTicketHandler webSocketTicketHandler;

    /**
     * Starts the backend websocket method by calling startThreads method of websocket handler.
     * @return a ResponseEntity with an HTTP status of ok.
     */
    @PostMapping("/start")
    public ResponseEntity<Void> startBackend() {
        webSocketTicketHandler.startThreads();
        return ResponseEntity.ok().build();
    }

    /**
     * Stops the backend websocket method by calling stop method of he websocket handler.
     * @return a ResponseEntity with an HTTP status of ok.
     */
    @PostMapping("/stop")
    public ResponseEntity<Void> stopBackend() {
        webSocketTicketHandler.stop();
        return ResponseEntity.ok().build();
    }
}
