package com.chamodh.RealtimeTicketingSystem.models;

import com.chamodh.RealtimeTicketingSystem.services.WebSocketTicketHandler;
import com.chamodh.RealtimeTicketingSystem.utils.Configuration;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Vendor class represents the vendor in the ticket purchasing prototype. Each vendor
 * adds tickets to a shared ticket pool in WebsocketHandler class at a specified release rate.
 * The class uses Reentrant locks and conditions to handle multiple vendors.
 */
public class Vendor implements Runnable {
    private final int vendorId;
    private final WebSocketTicketHandler ticketPool;
    private final Configuration config;
    private static int lastTicketId = 1;
    private static int currentTurn = 1;
    private final int numberOfVendors = 2;
    private static final Lock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();

    /**
     * Constructs a new Vendor instance with a unique ID, a specified WebSocketTicketHandler, and a Configuration.
     * @param vendorId
     * @param ticketPool
     * @param config
     */
    public Vendor(int vendorId, WebSocketTicketHandler ticketPool, Configuration config) {
        this.vendorId = vendorId;
        this.ticketPool = ticketPool;
        this.config = config;
    }

    /**
     * Returns the last ticket ID added and increments the ticket ID for the next addition.
     * @return the next ticket ID.
     */
    public static synchronized int getLastTicketId() {
        return lastTicketId++;
    }

    /**
     * Resets the last ticket ID and the current turn to their initial values when starting and stopping
     * the application.
     */
    public static void reset() {
        lastTicketId = 1;
        currentTurn = 1;
    }

    /**
     * Vendor attempts to add tickets to the ticket array in WebsocketTicketHandler class. The vendor awaits for
     * its turn to add tickets based on their Vendor ID and the current vendor in line. Once the vendor adds
     * ticket it rests for a time duration. This uses Reentrant locks and Conditions to handle the ticket adding
     * process by vendors among multiple vendors. The method keeps running until the current thread is interrupted
     * or the added ticket ID reaches total tickets needs to be added.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && lastTicketId <= config.getTotalTickets()) {
            try {
                lock.lock();
                while (vendorId != currentTurn) {
                    condition.await();
                }
                if (lastTicketId <= config.getTotalTickets()) {
                    ticketPool.addTicket(vendorId, getLastTicketId());
                    currentTurn = (currentTurn % numberOfVendors) + 1;
                    condition.signalAll();
                    Thread.sleep(config.getReleaseRate());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Vendor " + vendorId + " interrupted while sleeping.");
                break;
            } finally {
                lock.unlock();
            }
        }
        System.out.println("Vendor " + vendorId + " stopped running.");
    }
}
