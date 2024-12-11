package com.chamodh.RealtimeTicketingSystem.utils;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The configuration class represents the configuration settings for the ticketing system.
 * It includes parameters for ticketing system configurations.
 */
@Document(collection = "ticketingsystem")
public class Configuration {

    private int totalTickets;
    private int releaseRate;
    private int buyingRate;
    private int maxCapacity;

    /**
     * Constructs a new configuration instance with specified parameters.
     * @param totalTickets the total number of tickets are being added by vendors
     * @param releaseRate the rate at which vendors release tickets
     * @param buyingRate the rate of customers buying the release tickets
     * @param maxCapacity the maximum number of tickets that can be held in the ticket pool at once.
     */
    public Configuration(int totalTickets, int releaseRate, int buyingRate, int maxCapacity) {
        this.totalTickets = totalTickets;
        this.releaseRate = releaseRate;
        this.buyingRate = buyingRate;
        this.maxCapacity = maxCapacity;
    }

    /**
     * Returns the total number of tickets
     * @return the total number of tickets
     */
    public int getTotalTickets() {
        return totalTickets;
    }

    /**
     * Returns the releasing rate of the tickets by vendors
     * @return the releasing rate
     */
    public int getReleaseRate() {
        return releaseRate;
    }

    /**
     * Returns the rate at which vendors buying the released tickets
     * @return the buying rate of the tickets
     */
    public int getBuyingRate() {
        return buyingRate;
    }

    /**
     * Returns the maximum number of tickets that can be held in the system
     * @return the maximum number of tickets.
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }
}