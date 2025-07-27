package org.entities;

public class Stops {
    private String stationName;
    private String arrivalTime;
    private String departureTime;
    private String haltTime;
    private int platformNumber;

    public Stops(String stationName, String arrivalTime, String departureTime, String haltTime, int platformNumber) {
        this.stationName = stationName;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.haltTime = haltTime;
        this.platformNumber = platformNumber;
    }

    public String getStationName() {
        return stationName;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getHaltTime() {
        return haltTime;
    }

    public int getPlatformNumber() {
        return platformNumber;
    }
}
