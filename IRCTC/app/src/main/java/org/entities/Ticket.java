package org.entities;

import java.util.UUID;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Ticket {
    private String ticketId;
    private String userId;
    private String trainNumber;
    private LocalDate travelDate;
    private String source;
    private String destination;
    private String coachNumber;
    private int seatNumber;
    private String status;
    private int fare;

    static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Ticket(String userId, String trainNumber, String coachNumber, int seatNumber, String travelDateStr,
            String source,
            String destination, String status, int fare) {
        this.ticketId = UUID.randomUUID().toString();
        this.trainNumber = trainNumber;
        this.userId = userId;
        this.coachNumber = coachNumber;
        this.travelDate = LocalDate.parse(travelDateStr, DATE_FORMATTER);
        this.source = source;
        this.destination = destination;
        this.status = status;
        this.seatNumber = seatNumber;
        this.fare = fare;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getCoachNumber() {
        return coachNumber;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public String getStatus() {
        return status;
    }

    public int getFare() {
        return fare;
    }
}
