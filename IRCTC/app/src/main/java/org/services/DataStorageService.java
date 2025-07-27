package org.services;

import org.entities.*;
import java.io.*;
import java.util.*;

public class DataStorageService {
    private static final String DATA_DIR = "data";
    private static final String TRAINS_FILE = "trains.txt";
    private static final String USERS_FILE = "users.txt";
    private static final String TICKETS_FILE = "tickets.txt";
    private static final String JOURNEYS_FILE = "journeys.txt";
    
    public DataStorageService() {
        createDataDirectory();
    }
    
    private void createDataDirectory() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            boolean created = dataDir.mkdirs();
            if (created) {
                System.out.println("Data directory created: " + dataDir.getAbsolutePath());
            }
        }
    }
    
    // Train operations
    public void saveTrains(List<Train> trains) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIR + "/" + TRAINS_FILE))) {
            for (Train train : trains) {
                StringBuilder line = new StringBuilder();
                line.append(train.getTrainNumber()).append("|");
                line.append(String.join(",", train.getCoaches())).append("|");
                line.append(String.join(",", train.getSeats().stream().map(String::valueOf).toList()));
                writer.println(line.toString());
            }
            System.out.println("Trains saved successfully to " + DATA_DIR + "/" + TRAINS_FILE);
        } catch (IOException e) {
            System.err.println("Error saving trains: " + e.getMessage());
        }
    }
    
    public List<Train> loadTrains() {
        List<Train> trains = new ArrayList<>();
        File file = new File(DATA_DIR + "/" + TRAINS_FILE);
        if (!file.exists()) {
            System.out.println("No trains file found. Starting with empty train list.");
            return trains;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    String trainNumber = parts[0];
                    String[] coaches = parts[1].split(",");
                    String[] seatsStr = parts[2].split(",");
                    List<String> coachList = Arrays.asList(coaches);
                    List<Integer> seatList = new ArrayList<>();
                    for (String seat : seatsStr) {
                        seatList.add(Integer.parseInt(seat.trim()));
                    }
                    trains.add(new Train(trainNumber, coachList, seatList));
                }
            }
            System.out.println("Loaded " + trains.size() + " trains from file.");
        } catch (IOException e) {
            System.err.println("Error loading trains: " + e.getMessage());
        }
        return trains;
    }
    
    // User operations
    public void saveUsers(Map<String, User> users) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIR + "/" + USERS_FILE))) {
            for (User user : users.values()) {
                StringBuilder line = new StringBuilder();
                line.append(user.getUserId()).append("|");
                line.append(user.getName()).append("|");
                line.append(user.getEmail()).append("|");
                line.append(user.getHashedPassword()).append("|");
                line.append(user.getBookedTickets().size());
                writer.println(line.toString());
            }
            System.out.println("Users saved successfully to " + DATA_DIR + "/" + USERS_FILE);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
    
    public Map<String, User> loadUsers() {
        Map<String, User> users = new HashMap<>();
        File file = new File(DATA_DIR + "/" + USERS_FILE);
        if (!file.exists()) {
            System.out.println("No users file found. Starting with empty user list.");
            return users;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    String userId = parts[0];
                    String name = parts[1];
                    String email = parts[2];
                    String storedPassword = parts[3];
                    
                    // Check if the stored password is already hashed (64 characters for SHA-256)
                    String hashedPassword;
                    if (storedPassword.length() == 64 && storedPassword.matches("[a-f0-9]+")) {
                        // Password is already hashed
                        hashedPassword = storedPassword;
                    } else {
                        // Password is plain text, hash it now
                        hashedPassword = hashPassword(storedPassword);
                        System.out.println("Migrated plain text password to hashed for user: " + email);
                    }
                    
                    // Create user with the hashed password
                    User user = new User(name, email, hashedPassword, userId);
                    users.put(userId, user);
                }
            }
            System.out.println("Loaded " + users.size() + " users from file.");
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }
    
    // Simple password hashing using SHA-256 (same as in User class)
    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            // Fallback to simple hash if SHA-256 is not available
            return String.valueOf(password.hashCode());
        }
    }
    
    // Ticket operations
    public void saveTickets(Map<String, Ticket> tickets) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIR + "/" + TICKETS_FILE))) {
            for (Ticket ticket : tickets.values()) {
                StringBuilder line = new StringBuilder();
                line.append(ticket.getTicketId()).append("|");
                line.append(ticket.getUserId()).append("|");
                line.append(ticket.getTrainNumber()).append("|");
                line.append(ticket.getTravelDate()).append("|");
                line.append(ticket.getSource()).append("|");
                line.append(ticket.getDestination()).append("|");
                line.append(ticket.getCoachNumber()).append("|");
                line.append(ticket.getSeatNumber()).append("|");
                line.append(ticket.getStatus()).append("|");
                line.append(ticket.getFare());
                writer.println(line.toString());
            }
            System.out.println("Tickets saved successfully to " + DATA_DIR + "/" + TICKETS_FILE);
        } catch (IOException e) {
            System.err.println("Error saving tickets: " + e.getMessage());
        }
    }
    
    public Map<String, Ticket> loadTickets() {
        Map<String, Ticket> tickets = new HashMap<>();
        File file = new File(DATA_DIR + "/" + TICKETS_FILE);
        if (!file.exists()) {
            System.out.println("No tickets file found. Starting with empty ticket list.");
            return tickets;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 10) {
                    String ticketId = parts[0];
                    String userId = parts[1];
                    String trainNumber = parts[2];
                    String travelDate = parts[3];
                    String source = parts[4];
                    String destination = parts[5];
                    String coachNumber = parts[6];
                    int seatNumber = Integer.parseInt(parts[7]);
                    String status = parts[8];
                    int fare = Integer.parseInt(parts[9]);
                    
                    Ticket ticket = new Ticket(userId, trainNumber, coachNumber, 
                        seatNumber, travelDate, source, destination, status, fare);
                    tickets.put(ticketId, ticket);
                }
            }
            System.out.println("Loaded " + tickets.size() + " tickets from file.");
        } catch (IOException e) {
            System.err.println("Error loading tickets: " + e.getMessage());
        }
        return tickets;
    }
    
    // Journey operations - improved to handle stops
    public void saveJourneys(List<Journey> journeys) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIR + "/" + JOURNEYS_FILE))) {
            for (Journey journey : journeys) {
                StringBuilder line = new StringBuilder();
                line.append(journey.getJourneyNumber()).append("|");
                line.append(journey.getTrainNumber()).append("|");
                line.append(journey.getStarDate()).append("|");
                line.append(journey.getEndDate()).append("|");
                
                // Save stops information
                List<Stops> stops = journey.getStops();
                for (int i = 0; i < stops.size(); i++) {
                    Stops stop = stops.get(i);
                    if (i > 0) line.append(";");
                    line.append(stop.getStationName()).append(",");
                    line.append(stop.getArrivalTime()).append(",");
                    line.append(stop.getDepartureTime()).append(",");
                    line.append(stop.getHaltTime()).append(",");
                    line.append(stop.getPlatformNumber());
                }
                
                writer.println(line.toString());
            }
            System.out.println("Journeys saved successfully to " + DATA_DIR + "/" + JOURNEYS_FILE);
        } catch (IOException e) {
            System.err.println("Error saving journeys: " + e.getMessage());
        }
    }
    
    public List<Journey> loadJourneys() {
        List<Journey> journeys = new ArrayList<>();
        File file = new File(DATA_DIR + "/" + JOURNEYS_FILE);
        if (!file.exists()) {
            System.out.println("No journeys file found. Starting with empty journey list.");
            return journeys;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    String journeyNumber = parts[0];
                    String trainNumber = parts[1];
                    String startDate = parts[2];
                    String endDate = parts[3];
                    String stopsData = parts[4];
                    
                    // Create a basic train for the journey
                    List<String> coaches = Arrays.asList("A1", "A2", "B1", "B2");
                    List<Integer> seats = Arrays.asList(72, 72, 72, 72);
                    Train train = new Train(trainNumber, coaches, seats);
                    
                    Journey journey = new Journey(journeyNumber, startDate, endDate, train);
                    
                    // Load stops if available
                    if (!stopsData.isEmpty()) {
                        String[] stopsArray = stopsData.split(";");
                        for (String stopData : stopsArray) {
                            String[] stopParts = stopData.split(",");
                            if (stopParts.length >= 5) {
                                Stops stop = new Stops(
                                    stopParts[0], // station name
                                    stopParts[1], // arrival time
                                    stopParts[2], // departure time
                                    stopParts[3], // halt time
                                    Integer.parseInt(stopParts[4]) // platform number
                                );
                                journey.addStop(stop);
                            }
                        }
                    }
                    
                    journeys.add(journey);
                }
            }
            System.out.println("Loaded " + journeys.size() + " journeys from file.");
        } catch (IOException e) {
            System.err.println("Error loading journeys: " + e.getMessage());
        }
        return journeys;
    }
    
    // Save all data
    public void saveAllData(List<Train> trains, Map<String, User> users, 
                           Map<String, Ticket> tickets, List<Journey> journeys) {
        saveTrains(trains);
        saveUsers(users);
        saveTickets(tickets);
        saveJourneys(journeys);
        System.out.println("All data saved successfully!");
    }
    
    // Load all data
    public void loadAllData(List<Train> trains, Map<String, User> users, 
                           Map<String, Ticket> tickets, List<Journey> journeys) {
        List<Train> loadedTrains = loadTrains();
        Map<String, User> loadedUsers = loadUsers();
        Map<String, Ticket> loadedTickets = loadTickets();
        List<Journey> loadedJourneys = loadJourneys();
        
        trains.clear();
        trains.addAll(loadedTrains);
        
        users.clear();
        users.putAll(loadedUsers);
        
        tickets.clear();
        tickets.putAll(loadedTickets);
        
        journeys.clear();
        journeys.addAll(loadedJourneys);
        
        System.out.println("All data loaded successfully!");
    }
} 