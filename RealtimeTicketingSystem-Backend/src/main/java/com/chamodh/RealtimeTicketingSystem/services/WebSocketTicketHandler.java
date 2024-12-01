package com.chamodh.RealtimeTicketingSystem.services;

import com.chamodh.RealtimeTicketingSystem.models.Customer;
import com.chamodh.RealtimeTicketingSystem.models.Vendor;
import com.chamodh.RealtimeTicketingSystem.utils.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class WebSocketTicketHandler extends TextWebSocketHandler {

    private List<Integer> ticketPool = Collections.synchronizedList(new ArrayList<>());
    private WebSocketSession currentSession;
    private final ConfigurationService configService;
    private final Configuration config;

    private volatile boolean running = false; // Initially not running
    private List<Thread> threads = new ArrayList<>(); // Track threads

    public WebSocketTicketHandler(ConfigurationService configService) {
        this.configService = configService;
        this.config = configService.readConfigurationFile();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.currentSession = session;
        session.sendMessage(new TextMessage("Connected websocket."));
        System.out.println("Connected to websocket.");

        startThreads();
    }

    // Method to start all threads
    public void startThreads() {
        running = true;
        ticketPool.clear(); // Clear the ticket pool to reset state
        threads.clear();

        // Reset static fields
        Customer.reset();
        Vendor.reset();

        Thread vendor1 = new Thread(new Vendor(1, this, config));
        Thread vendor2 = new Thread(new Vendor(2, this, config));
        Thread customer1 = new Thread(new Customer(1, this, config));
        Thread customer2 = new Thread(new Customer(2, this, config));

        threads.add(vendor1); // Track the threads
        threads.add(vendor2);
        threads.add(customer1);
        threads.add(customer2);

        vendor1.start();
        vendor2.start();
        customer1.start();
        customer2.start();
    }

    // Method to stop all threads and close WebSocket
    public void stop() {
        running = false;

        for (Thread thread : threads) {
            thread.interrupt();
        }

        for (Thread thread : threads) {
            try {
                thread.join(); // Wait for each thread to finish
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted while waiting to join.");
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("All vendor and customer threads successfully interrupted.");

        if (currentSession != null && currentSession.isOpen()) {
            try {
                currentSession.close(CloseStatus.NORMAL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        threads.clear(); // Clear the threads list
    }

    public boolean isRunning() {
        return running;
    }

    private void sendMessage(String message) {
        if (currentSession != null && currentSession.isOpen()) {
            try {
                currentSession.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                System.out.println("Error sending message.");
            }
        }
    }

    public void addTicket(int vendorId, int lastTicketId) {
        synchronized (this) {
            if (!running) return; // Check if running

            try {
                if (ticketPool.size() == config.getMaxCapacity()) {
                    String message = "Ticket pool reached max capacity.";
                    sendMessage(message);
                    System.out.println(message);
                    wait();
                }
            } catch (InterruptedException e) {
                String message = "Vendor Interrupted Waiting";
                sendMessage(message);
                System.out.println(message);
            }

            ticketPool.add(lastTicketId);
            String message = "Vendor " + vendorId + " added a ticket: " + lastTicketId + " Ticket pool size: " + ticketPool.size();
            sendMessage(message);
            System.out.println(message);

            sendMessage(String.valueOf(ticketPool.size()));

            notifyAll();
        }
    }

    public void buyTicket(int customerId, int lastTicket) {
        synchronized (this) {
            if (!running) return; // Check if running

            if (ticketPool.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.out.println("Customer " + customerId + " was interrupted waiting.");
                }
                String message = "Customer " + customerId + " says: No tickets available to buy.";
                sendMessage(message);
                System.out.println(message);
                return;
            }
            ticketPool.removeLast();

            String message = "Customer " + customerId + " bought ticketID: " + lastTicket + " pool size: " + ticketPool.size();
            sendMessage(message);
            System.out.println(message);

            sendMessage(String.valueOf(ticketPool.size()));

            notifyAll();
        }
    }
}
