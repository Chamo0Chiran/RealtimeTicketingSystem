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

/**
 * The WebsocketTicketHandler class handles Websocket connections and manages the
 * ticket purchasing system.
 * It initializes customer and vendor threads, manages ticket pool and broadcasts live
 * status of the ticket pool to frontend via websockets.
 */
@Component
public class WebSocketTicketHandler extends TextWebSocketHandler {

    private List<Integer> ticketPool = Collections.synchronizedList(new ArrayList<>());
    private WebSocketSession currentSession;
    private final ConfigurationService configService;
    private final Configuration config;
    private boolean webSocketConnected = false;

    private volatile boolean running = false;
    private List<Thread> threads = new ArrayList<>();

    /**
     * Constructs a new WebsocketTicketHandle instance with specified configurations retrieved
     * by Configuration service.
     * @param configService
     */
    public WebSocketTicketHandler(ConfigurationService configService) {
        this.configService = configService;
        this.config = configService.readConfigurationFile();
    }

    /**
     * Called after a websocket connection is established.
     * Sets the current websocket session, marks the websocket as connected, and starts
     * the customer and vendor threads by calling startThreads() method.
     * @param session the websocket session that is established
     * @throws Exception if an error occurs during the connection establishment.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.currentSession = session;
        webSocketConnected = true;
        broadcastMessage("WebSocket Connected");
        System.out.println("WebSocket Connected");

        startThreads();
    }

    /**
     * Creates and starts threads for Vendor and Customer instances for the ticket purchasing
     * system prototype.
     * Clears the ticket pool and initializes the threads if Websocket is connected.
     * The method resets Customer and Vendor with reset() methods.
     */
    public synchronized void startThreads() {
        if (!webSocketConnected) {
            System.out.println("Waiting for WebSocket connection...");
            return;
        }
        running = true;
        ticketPool.clear();
        threads.clear();

        Customer.reset();
        Vendor.reset();

        Thread vendor1 = new Thread(new Vendor(1, this, config));
        Thread vendor2 = new Thread(new Vendor(2, this, config));
        Thread customer1 = new Thread(new Customer(1, this, config));
        Thread customer2 = new Thread(new Customer(2, this, config));

        threads.add(vendor1);
        threads.add(vendor2);
        threads.add(customer1);
        threads.add(customer2);

        vendor1.start();
        vendor2.start();
        customer1.start();
        customer2.start();

        System.out.println("Threads started after WebSocket connection.");
    }

    /**
     * Stops all running vendor and customer threads and closes websocket session.
     * Interrupts each threads after waiting for them to join.
     */
    public void stop() {
        running = false;
        for (Thread thread : threads) {
            thread.interrupt();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
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
                System.out.println("Couldn't close the current session successfully.");
            }
        }

        threads.clear();
    }

    /**
     * Sends a message to the current websocket session if it is open.
     * @param message message with current status of the ticket pool.
     */
    private void broadcastMessage(String message) {
        if (currentSession != null && currentSession.isOpen()) {
            try {
                currentSession.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                System.out.println("Error sending message.");
            }
        }
    }

    /**
     * Adds a ticket to ticket pool array. If the ticket pool reaches max capacity broadcasts a message
     * saying vendors are waiting since the ticket pool is at max capacity. The method broadcasts a message
     * when the vendors added a ticket with the ticket pool size.
     * @param vendorId the ID of the vendor adding the ticket.
     * @param lastTicketId the ID of the last ticket being added.
     */
    public synchronized void addTicket(int vendorId, int lastTicketId) {
            if (!running) return;

            try {
                if (ticketPool.size() == config.getMaxCapacity()) {
                    String message = "Ticket pool reached max capacity.";
                    broadcastMessage(message);
                    System.out.println(message);
                    wait();
                } else {
                    ticketPool.add(lastTicketId);
                    String message = "Vendor " + vendorId + " added a ticket: " + lastTicketId + " Ticket pool size: " + ticketPool.size();
                    System.out.println(message);
                    broadcastMessage(message);
                    broadcastMessage(String.valueOf(ticketPool.size()));

                    notifyAll();
                }
            } catch (InterruptedException e) {
                String message = "Vendor Interrupted Waiting";
                broadcastMessage(message);
                System.out.println(message);
            }
            notifyAll();
    }

    /**
     * Removes a ticket from the ticket pool for a customer. If no tickets available it broadcasts
     * a message saying there are no tickets available. Customer waits till there's a ticket in the
     * ticket pool to buy. The method broadcasts a message when a customer bought a ticket.
     * @param customerId the ID of the customer buying the ticket.
     */
    public synchronized void buyTicket(int customerId) {
        if (!running) return;

        if (ticketPool.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Customer " + customerId + " was interrupted waiting.");
            }
            String message = "Customer " + customerId + " says: No tickets available to buy.";
            broadcastMessage(message);
            System.out.println(message);
            return;
        }

        int lastIndex = ticketPool.size() - 1;
        int ticketId = ticketPool.remove(lastIndex);

        String message = "Customer " + customerId + " bought ticket ID: " + ticketId + " Pool size: " + ticketPool.size();
        broadcastMessage(message);
        System.out.println(message);

        broadcastMessage(String.valueOf(ticketPool.size()));

        notifyAll();
    }

}
