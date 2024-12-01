package com.chamodh.RealtimeTicketingSystem.models;

import com.chamodh.RealtimeTicketingSystem.services.WebSocketTicketHandler;
import com.chamodh.RealtimeTicketingSystem.utils.Configuration;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Vendor implements Runnable {
    private final int vendorId;
    private final WebSocketTicketHandler ticketPool;
    private final Configuration config;
    private static int lastTicketId = 1;
    private static int currentTurn = 1;
    private final int numberOfVendors = 2;
    private static final Lock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();

    public Vendor(int vendorId, WebSocketTicketHandler ticketPool, Configuration config) {
        this.vendorId = vendorId;
        this.ticketPool = ticketPool;
        this.config = config;
    }

    public int getVendorId() {
        return vendorId;
    }

    public static synchronized int getLastTicketId() {
        return lastTicketId++;
    }

    public static void reset() {
        lastTicketId = 1;
        currentTurn = 1;
    }

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
                Thread.currentThread().interrupt(); // Re-interrupt the thread
                System.out.println("Vendor " + vendorId + " interrupted while sleeping.");
                break; // Exit the loop
            } finally {
                lock.unlock();
            }
        }
        System.out.println("Vendor " + vendorId + " stopped running.");
    }
}
