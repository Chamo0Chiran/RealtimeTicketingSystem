package com.chamodh.RealtimeTicketingSystem.models;

import com.chamodh.RealtimeTicketingSystem.services.WebSocketTicketHandler;
import com.chamodh.RealtimeTicketingSystem.utils.Configuration;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Customer class represents the customer in the ticket purchasing system. Each customer
 * attempts to buy tickets from a shared ticket pool in WebSockethandler at a specified rate.
 * The class uses ReentrantLocks and Conditions to manage the order of ticket buying among multiple
 * customers.
 */
public class Customer implements Runnable {
    private final int customerId;
    private final WebSocketTicketHandler ticketPool;
    private final Configuration config;
    private final int numberOfCustomers = 2;
    private static int currentCustomer = 1;
    private static int boughtTicketId = 1;
    private static final Lock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();

    /**
     * Constructs a new customer instance with a unique ID, a specified WebsocketHandler
     * @param customerId the unique ID of the customer
     * @param ticketPool the ticket pool from the WebsocketHandler class
     * @param config the configuration settings containing the buying rate.
     */
    public Customer(int customerId, WebSocketTicketHandler ticketPool, Configuration config) {
        this.customerId = customerId;
        this.ticketPool = ticketPool;
        this.config = config;
    }

    /**
     * Resets the current customer and the bought ticket ID to their initial values when stopping and
     * restarting the application.
     */
    public static void reset() {
        currentCustomer = 1;
        boughtTicketId = 1;
    }

    /**
     * Customer attempts to buy tickets from the WebsocketTicketHandler ticket pool array.
     * Once the customer buys tickets it rests for a time duration before attempting to buy
     * another. This uses Reentrant locks and Conditions to manage order of ticket buying among
     * multiple customers.
     */
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
                ticketPool.buyTicket(customerId);
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
