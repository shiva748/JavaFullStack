package org.entities;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class Journey {
    private String JourneyNumber;
    private List<Stops> stops;
    private List<String> coaches;
    private List<List<String>> seats;
    private LocalDate starDate;
    private LocalDate endDate;
    private String trainNumber;

    public Journey(String journeyNumber, String startDate, String endDate, Train train) {
        this.JourneyNumber = journeyNumber;
        this.stops = new ArrayList<>();
        this.starDate = LocalDate.parse(startDate, Ticket.DATE_FORMATTER);
        this.endDate = LocalDate.parse(endDate, Ticket.DATE_FORMATTER);
        this.trainNumber = train.getTrainNumber();
        this.coaches = new ArrayList<>(train.getCoaches());
        this.seats = new ArrayList<>();
        for (int i = 0; i < train.getCoaches().size(); i++) {
            this.seats.add(new ArrayList<>(train.getSeats().get(i)));
        }
    }
    public String getJourneyNumber() {
        return JourneyNumber;
    }
    public List<Stops> getStops() {
        return stops;
    }
    public List<String> getCoaches() {
        return coaches;
    }
    public List<List<String>> getSeats() {
        return seats;
    }
    public LocalDate getStarDate() {
        return starDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }
    public String getTrainNumber() {
        return trainNumber;
    }
    public void addStop(Stops stop) {
        this.stops.add(stop);
    }

}
