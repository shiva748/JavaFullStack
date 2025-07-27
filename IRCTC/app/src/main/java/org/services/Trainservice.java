package org.services;

import java.util.ArrayList;
import java.util.List;
import org.entities.*;

public class Trainservice {
    private List<Train> trains;
    private List<Journey> journeys;
    private DataStorageService dataStorage;

    public Trainservice() {
        this.trains = new ArrayList<>();
        this.journeys = new ArrayList<>();
        this.dataStorage = new DataStorageService();
    }

    public void addTrain(String trainNumber, List<String> coaches, List<Integer> seats) {
        Train train = new Train(trainNumber, new ArrayList<>(coaches), new ArrayList<>(seats));
        trains.add(train);
        dataStorage.saveTrains(trains);
        System.out.println("Train " + trainNumber + " added and saved successfully!");
    }

    public void addJourney(String journeyNumber, String startDate, String endDate, Train train, List<Stops> stops) {
        Journey journey = new Journey(journeyNumber, startDate, endDate, train);
        for (Stops stop : stops) {
            journey.addStop(stop);
        }
        journeys.add(journey);
        dataStorage.saveJourneys(journeys);
        System.out.println("Journey " + journeyNumber + " added and saved successfully!");
    }

    public Journey searchJourney(Stops stop) {
        for (Journey journey : journeys) {
            for (Stops s : journey.getStops()) {
                if (s.getStationName().equals(stop.getStationName())) {
                    return journey;
                }
            }
        }
        return null;
    }

    public List<Journey> searchJourneysByRoute(String source, String destination) {
        List<Journey> matchingJourneys = new ArrayList<>();
        
        for (Journey journey : journeys) {
            boolean hasSource = false;
            boolean hasDestination = false;
            
            for (Stops stop : journey.getStops()) {
                if (stop.getStationName().equalsIgnoreCase(source)) {
                    hasSource = true;
                }
                if (stop.getStationName().equalsIgnoreCase(destination)) {
                    hasDestination = true;
                }
            }
            
            if (hasSource && hasDestination) {
                matchingJourneys.add(journey);
            }
        }
        
        return matchingJourneys;
    }

    public List<Train> getAllTrains() {
        return new ArrayList<>(trains);
    }

    public Train getTrainByNumber(String trainNumber) {
        return trains.stream()
                .filter(train -> train.getTrainNumber().equals(trainNumber))
                .findFirst()
                .orElse(null);
    }

    public List<Journey> getAllJourneys() {
        return new ArrayList<>(journeys);
    }

    public boolean isSeatAvailable(String trainNumber, String coachNumber, int seatNumber, String travelDate) {
        // Simple availability check - in a real system, this would check against booked seats
        Train train = getTrainByNumber(trainNumber);
        if (train == null) return false;
        
        List<String> coaches = train.getCoaches();
        List<Integer> seats = train.getSeats();
        
        for (int i = 0; i < coaches.size(); i++) {
            if (coaches.get(i).equals(coachNumber)) {
                return seatNumber <= seats.get(i);
            }
        }
        return false;
    }

    public List<String> getAvailableCoaches(String trainNumber) {
        Train train = getTrainByNumber(trainNumber);
        if (train == null) return new ArrayList<>();
        return new ArrayList<>(train.getCoaches());
    }

    public int getMaxSeatsForCoach(String trainNumber, String coachNumber) {
        Train train = getTrainByNumber(trainNumber);
        if (train == null) return 0;
        
        List<String> coaches = train.getCoaches();
        List<Integer> seats = train.getSeats();
        
        for (int i = 0; i < coaches.size(); i++) {
            if (coaches.get(i).equals(coachNumber)) {
                return seats.get(i);
            }
        }
        return 0;
    }

    public void displayTrainSchedule(String trainNumber) {
        Train train = getTrainByNumber(trainNumber);
        if (train == null) {
            System.out.println("Train not found.");
            return;
        }
        
        System.out.println("\n=== Train Schedule for " + trainNumber + " ===");
        System.out.println("Coach | Max Seats");
        System.out.println("------|-----------");
        
        List<String> coaches = train.getCoaches();
        List<Integer> seats = train.getSeats();
        
        for (int i = 0; i < coaches.size(); i++) {
            System.out.printf("%-5s | %-9d%n", coaches.get(i), seats.get(i));
        }
    }

    public void displayJourneyDetails(String journeyNumber) {
        Journey journey = journeys.stream()
                .filter(j -> j.getJourneyNumber().equals(journeyNumber))
                .findFirst()
                .orElse(null);
        
        if (journey == null) {
            System.out.println("Journey not found.");
            return;
        }
        
        System.out.println("\n=== Journey Details ===");
        System.out.println("Journey Number: " + journey.getJourneyNumber());
        System.out.println("Train Number: " + journey.getTrainNumber());
        System.out.println("Start Date: " + journey.getStarDate());
        System.out.println("End Date: " + journey.getEndDate());
        
        System.out.println("\nStops:");
        System.out.println("Station | Arrival | Departure | Platform");
        System.out.println("--------|---------|-----------|---------");
        
        for (Stops stop : journey.getStops()) {
            System.out.printf("%-7s | %-7s | %-9s | %-7d%n",
                stop.getStationName(),
                stop.getArrivalTime(),
                stop.getDepartureTime(),
                stop.getPlatformNumber());
        }
    }

    // Data management methods
    public void loadData() {
        List<Train> loadedTrains = dataStorage.loadTrains();
        List<Journey> loadedJourneys = dataStorage.loadJourneys();
        
        if (!loadedTrains.isEmpty()) {
            trains.clear();
            trains.addAll(loadedTrains);
            System.out.println("Loaded " + loadedTrains.size() + " trains from file.");
        }
        
        if (!loadedJourneys.isEmpty()) {
            journeys.clear();
            journeys.addAll(loadedJourneys);
            System.out.println("Loaded " + loadedJourneys.size() + " journeys from file.");
        }
    }

    public void saveData() {
        dataStorage.saveTrains(trains);
        dataStorage.saveJourneys(journeys);
    }

    public void deleteTrain(String trainNumber) {
        Train trainToDelete = getTrainByNumber(trainNumber);
        if (trainToDelete == null) {
            System.out.println("Train not found!");
            return;
        }
        
        trains.removeIf(train -> train.getTrainNumber().equals(trainNumber));
        // Also remove journeys associated with this train
        journeys.removeIf(journey -> journey.getTrainNumber().equals(trainNumber));
        
        dataStorage.saveTrains(trains);
        dataStorage.saveJourneys(journeys);
        System.out.println("Train " + trainNumber + " deleted successfully!");
    }

    public void deleteJourney(String journeyNumber) {
        Journey journeyToDelete = journeys.stream()
                .filter(j -> j.getJourneyNumber().equals(journeyNumber))
                .findFirst()
                .orElse(null);
                
        if (journeyToDelete == null) {
            System.out.println("Journey not found!");
            return;
        }
        
        journeys.removeIf(journey -> journey.getJourneyNumber().equals(journeyNumber));
        dataStorage.saveJourneys(journeys);
        System.out.println("Journey " + journeyNumber + " deleted successfully!");
    }

    public void listAllTrains() {
        if (trains.isEmpty()) {
            System.out.println("No trains available.");
            return;
        }
        
        System.out.println("\n=== All Trains ===");
        System.out.println("Train No | Coaches | Total Seats");
        System.out.println("---------|---------|-------------");
        
        for (Train train : trains) {
            int totalSeats = train.getSeats().stream().mapToInt(Integer::intValue).sum();
            System.out.printf("%-8s | %-7s | %-11d%n", 
                train.getTrainNumber(), 
                train.getCoaches().size(), 
                totalSeats);
        }
    }

    public void listAllJourneys() {
        if (journeys.isEmpty()) {
            System.out.println("No journeys available.");
            return;
        }
        
        System.out.println("\n=== All Journeys ===");
        System.out.println("Journey No | Train No | Start Date | End Date | Stops");
        System.out.println("------------|----------|------------|----------|-------");
        
        for (Journey journey : journeys) {
            System.out.printf("%-11s | %-8s | %-10s | %-8s | %-5d%n",
                journey.getJourneyNumber(),
                journey.getTrainNumber(),
                journey.getStarDate(),
                journey.getEndDate(),
                journey.getStops().size());
        }
    }
}
