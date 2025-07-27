package org.entities;

import java.util.List;

public class Train {
    private String trainNumber;
    private List<String> coaches;
    private List<Integer> seats;

    public Train(String trainNumber, List<String> coaches, List<Integer> seats) {
        this.trainNumber = trainNumber;
        this.coaches = coaches;
        this.seats = seats;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public List<String> getCoaches() {
        return coaches;
    }

    public List<Integer> getSeats() {
        return seats;
    }
}
