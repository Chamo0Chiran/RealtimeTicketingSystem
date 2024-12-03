package com.chamodh.RealtimeTicketingSystem.models;

import com.chamodh.RealtimeTicketingSystem.services.WebSocketTicketHandler;
import com.chamodh.RealtimeTicketingSystem.utils.Configuration;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Customer implements Runnable {
    private final int customerId;
    private final WebSocketTicketHandler ticketPool;
    private final Configuration config;
    private final int numberOfCustomers = 2;
    private static int currentCustomer = 1;
    private static int boughtTicketId = 1;
    private static final Lock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();

    public Customer(int customerId, WebSocketTicketHandler ticketPool, Configuration config) {
        this.customerId = customerId;
        this.ticketPool = ticketPool;
        this.config = config;
    }

    public int getCustomerId() {
        return customerId;
    }

    public static int getLastTicketId() {
        return boughtTicketId++;
    }

    public static void reset() {
        currentCustomer = 1;
        boughtTicketId = 1;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && boughtTicketId < config.getTotalTickets()) {
            try {
                lock.lock();
                while (customerId != currentCustomer) {
                    condition.await();
                }
                currentCustomer = (currentCustomer % numberOfCustomers) + 1;
                condition.signalAll();
                Thread.sleep(config.getBuyingRate());
                ticketPool.buyTicket(customerId, getLastTicketId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Customer " + customerId + " interrupted while sleeping.");
                break;
            } finally {
                lock.unlock();
            }
        }
        System.out.println("Customer " + customerId + " stopped running.");
    }
}
