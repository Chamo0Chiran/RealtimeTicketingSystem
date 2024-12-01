package com.chamodh.RealtimeTicketingSystem.utils;

public class Configuration {

    private int totalTickets;
    private int releaseRate;
    private int buyingRate;
    private int maxCapacity;

    public Configuration(int totalTickets, int releaseRate, int buyingRate, int maxCapacity) {
        this.totalTickets = totalTickets;
        this.releaseRate = releaseRate;
        this.buyingRate = buyingRate;
        this.maxCapacity = maxCapacity;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public int getReleaseRate() {
        return releaseRate;
    }

    public int getBuyingRate() {
        return buyingRate;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }
}