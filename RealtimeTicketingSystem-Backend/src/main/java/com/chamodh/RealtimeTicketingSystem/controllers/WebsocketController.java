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
public class WebsocketController {

    @Autowired
    private WebSocketTicketHandler webSocketTicketHandler;

    @PostMapping("/start")
    public ResponseEntity<Void> startBackend() {
        webSocketTicketHandler.startThreads();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/stop")
    public ResponseEntity<Void> stopBackend() {
        webSocketTicketHandler.stop();
        return ResponseEntity.ok().build();
    }
}
