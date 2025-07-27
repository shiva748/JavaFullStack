package org.example;

import org.entities.*;
import org.services.Trainservice;
import org.services.DataStorageService;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class App {
    private static Trainservice trainService;
    private static Map<String, User> users;
    private static Map<String, Ticket> tickets;
    private static Scanner scanner;
    private static DataStorageService dataStorage;

    public static void main(String[] args) {
        initializeSystem();
        showMainMenu();
    }

    private static void initializeSystem() {
        trainService = new Trainservice();
        users = new HashMap<>();
        tickets = new HashMap<>();
        scanner = new Scanner(System.in);
        dataStorage = new DataStorageService();
        
        // Load existing data or initialize with sample data
        loadExistingData();
        
        // Add shutdown hook to save data when application exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nSaving data before exit...");
            saveAllData();
        }));
    }



    private static User findUserByEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null;
    }

    private static void loadExistingData() {
        System.out.println("Loading existing data...");
        
        // Load trains and journeys
        trainService.loadData();
        
        // Load users and tickets
        Map<String, User> loadedUsers = dataStorage.loadUsers();
        Map<String, Ticket> loadedTickets = dataStorage.loadTickets();
        
        users.putAll(loadedUsers);
        tickets.putAll(loadedTickets);
        
        System.out.println("Data loaded successfully!");
        System.out.println("Loaded " + users.size() + " users and " + tickets.size() + " tickets.");
        System.out.println("Loaded " + trainService.getAllTrains().size() + " trains and " + trainService.getAllJourneys().size() + " journeys.");
    }

    private static void saveAllData() {
        System.out.println("Saving all data...");
        dataStorage.saveAllData(trainService.getAllTrains(), users, tickets, trainService.getAllJourneys());
    }

    private static void showAdminPanel() {
        while (true) {
            System.out.println("\n=== Admin Panel ===");
            System.out.println("1. Add New Train");
            System.out.println("2. Add New Journey");
            System.out.println("3. List All Trains");
            System.out.println("4. List All Journeys");
            System.out.println("5. Delete Train");
            System.out.println("6. Delete Journey");
            System.out.println("7. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addNewTrain();
                    break;
                case 2:
                    addNewJourney();
                    break;
                case 3:
                    trainService.listAllTrains();
                    break;
                case 4:
                    trainService.listAllJourneys();
                    break;
                case 5:
                    deleteTrain();
                    break;
                case 6:
                    deleteJourney();
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addNewTrain() {
        System.out.println("\n=== Add New Train ===");
        System.out.print("Enter train number: ");
        String trainNumber = scanner.nextLine();
        
        if (trainService.getTrainByNumber(trainNumber) != null) {
            System.out.println("Train already exists!");
            return;
        }

        System.out.print("Enter number of coaches: ");
        int coachCount = scanner.nextInt();
        scanner.nextLine();

        List<String> coaches = new ArrayList<>();
        List<Integer> seats = new ArrayList<>();

        for (int i = 0; i < coachCount; i++) {
            System.out.print("Enter coach " + (i + 1) + " name (e.g., A1, B1): ");
            String coachName = scanner.nextLine();
            System.out.print("Enter number of seats for " + coachName + ": ");
            int seatCount = scanner.nextInt();
            scanner.nextLine();
            
            coaches.add(coachName);
            seats.add(seatCount);
        }

        trainService.addTrain(trainNumber, coaches, seats);
        System.out.println("Train added successfully!");
    }

    private static void addNewJourney() {
        System.out.println("\n=== Add New Journey ===");
        System.out.print("Enter journey number: ");
        String journeyNumber = scanner.nextLine();
        
        System.out.print("Enter train number: ");
        String trainNumber = scanner.nextLine();
        
        Train train = trainService.getTrainByNumber(trainNumber);
        if (train == null) {
            System.out.println("Train not found!");
            return;
        }

        System.out.print("Enter start date (yyyy-MM-dd): ");
        String startDate = scanner.nextLine();
        System.out.print("Enter end date (yyyy-MM-dd): ");
        String endDate = scanner.nextLine();

        System.out.print("Enter number of stops: ");
        int stopCount = scanner.nextInt();
        scanner.nextLine();

        List<Stops> stops = new ArrayList<>();
        for (int i = 0; i < stopCount; i++) {
            System.out.println("\nStop " + (i + 1) + ":");
            System.out.print("Station name: ");
            String stationName = scanner.nextLine();
            System.out.print("Arrival time (HH:mm): ");
            String arrivalTime = scanner.nextLine();
            System.out.print("Departure time (HH:mm): ");
            String departureTime = scanner.nextLine();
            System.out.print("Halt time (minutes): ");
            String haltTime = scanner.nextLine();
            System.out.print("Platform number: ");
            int platformNumber = scanner.nextInt();
            scanner.nextLine();

            stops.add(new Stops(stationName, arrivalTime, departureTime, haltTime, platformNumber));
        }

        trainService.addJourney(journeyNumber, startDate, endDate, train, stops);
        System.out.println("Journey added successfully!");
    }

    private static void deleteTrain() {
        System.out.println("\n=== Delete Train ===");
        trainService.listAllTrains();
        System.out.print("Enter train number to delete: ");
        String trainNumber = scanner.nextLine();
        
        Train train = trainService.getTrainByNumber(trainNumber);
        if (train == null) {
            System.out.println("Train not found!");
            return;
        }

        trainService.deleteTrain(trainNumber);
        System.out.println("Train deleted successfully!");
    }

    private static void deleteJourney() {
        System.out.println("\n=== Delete Journey ===");
        trainService.listAllJourneys();
        System.out.print("Enter journey number to delete: ");
        String journeyNumber = scanner.nextLine();
        
        trainService.deleteJourney(journeyNumber);
        System.out.println("Journey deleted successfully!");
    }

    private static void showMainMenu() {
        while (true) {
            System.out.println("\n=== IRCTC Railway Reservation System ===");
            System.out.println("1. User Registration");
            System.out.println("2. User Login");
            System.out.println("3. Search Trains");
            System.out.println("4. Book Ticket");
            System.out.println("5. View My Tickets");
            System.out.println("6. Admin Panel (Manage Trains/Journeys)");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    userRegistration();
                    break;
                case 2:
                    userLogin();
                    break;
                case 3:
                    searchTrains();
                    break;
                case 4:
                    bookTicket();
                    break;
                case 5:
                    viewMyTickets();
                    break;
                case 6:
                    showAdminPanel();
                    break;
                case 7:
                    System.out.println("Thank you for using IRCTC!");
                    saveAllData();
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void userRegistration() {
        System.out.println("\n=== User Registration ===");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // Check if email already exists
        if (findUserByEmail(email) != null) {
            System.out.println("Email already registered. Please use a different email.");
            return;
        }

        User user = new User(name, email, password);
        users.put(user.getUserId(), user);
        
        // Save users immediately
        dataStorage.saveUsers(users);
        
        System.out.println("Registration successful! You can now login with your email.");
        System.out.println("User ID: " + user.getUserId());
    }

    private static void userLogin() {
        System.out.println("\n=== User Login ===");
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = findUserByEmail(email);
        if (user != null && user.checkPassword(password)) {
            System.out.println("Login successful! Welcome, " + user.getName());
            showUserMenu(user);
        } else {
            System.out.println("Invalid email or password.");
        }
    }

    private static void showUserMenu(User user) {
        while (true) {
            System.out.println("\n=== User Menu ===");
            System.out.println("1. Search Trains");
            System.out.println("2. Book Ticket");
            System.out.println("3. View My Tickets");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    searchTrains();
                    break;
                case 2:
                    bookTicketForUser(user);
                    break;
                case 3:
                    viewUserTickets(user);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void searchTrains() {
        System.out.println("\n=== Search Trains ===");
        System.out.print("Enter source station: ");
        String source = scanner.nextLine();
        System.out.print("Enter destination station: ");
        String destination = scanner.nextLine();
        System.out.print("Enter travel date (yyyy-MM-dd): ");
        String travelDate = scanner.nextLine();

        List<Journey> matchingJourneys = trainService.searchJourneysByRoute(source, destination);
        
        if (matchingJourneys.isEmpty()) {
            System.out.println("No trains found for the specified route.");
            return;
        }

        System.out.println("\nAvailable Trains:");
        System.out.println("Journey No | Train No | Source | Destination | Date");
        System.out.println("------------|----------|--------|-------------|------");
        
        for (Journey journey : matchingJourneys) {
            System.out.printf("%-11s | %-8s | %-6s | %-11s | %s%n",
                journey.getJourneyNumber(),
                journey.getTrainNumber(),
                source,
                destination,
                travelDate);
        }
    }

    private static void bookTicket() {
        System.out.println("\n=== Book Ticket ===");
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        
        User user = findUserByEmail(email);
        if (user == null) {
            System.out.println("User not found. Please register first.");
            return;
        }

        bookTicketForUser(user);
    }

    private static void bookTicketForUser(User user) {
        System.out.println("\n=== Book Ticket ===");
        System.out.print("Enter train number: ");
        String trainNumber = scanner.nextLine();
        System.out.print("Enter travel date (yyyy-MM-dd): ");
        String travelDate = scanner.nextLine();
        System.out.print("Enter source station: ");
        String source = scanner.nextLine();
        System.out.print("Enter destination station: ");
        String destination = scanner.nextLine();
        System.out.print("Enter coach number: ");
        String coachNumber = scanner.nextLine();
        System.out.print("Enter seat number: ");
        int seatNumber = scanner.nextInt();
        scanner.nextLine();

        // Generate a random fare
        int fare = 500 + (int)(Math.random() * 1000);

        Ticket ticket = new Ticket(user.getUserId(), trainNumber, coachNumber, 
            seatNumber, travelDate, source, destination, "CONFIRMED", fare);
        
        tickets.put(ticket.getTicketId(), ticket);
        user.addTicket(ticket.getTicketId());
        
        // Save tickets and users immediately
        dataStorage.saveTickets(tickets);
        dataStorage.saveUsers(users);
        
        System.out.println("\n=== Ticket Booked Successfully ===");
        System.out.println("Ticket ID: " + ticket.getTicketId());
        System.out.println("Train: " + ticket.getTrainNumber());
        System.out.println("From: " + ticket.getSource() + " To: " + ticket.getDestination());
        System.out.println("Date: " + ticket.getTravelDate());
        System.out.println("Coach: " + ticket.getCoachNumber() + " Seat: " + ticket.getSeatNumber());
        System.out.println("Fare: ₹" + ticket.getFare());
        System.out.println("Status: " + ticket.getStatus());
    }

    private static void viewMyTickets() {
        System.out.println("\n=== View My Tickets ===");
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        
        User user = findUserByEmail(email);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        viewUserTickets(user);
    }

    private static void viewUserTickets(User user) {
        System.out.println("\n=== My Tickets ===");
        boolean found = false;
        
        for (Ticket ticket : tickets.values()) {
            if (ticket.getUserId().equals(user.getUserId())) {
                if (!found) {
                    System.out.println("Ticket ID | Train | From | To | Date | Status");
                    System.out.println("----------|-------|------|----|------|--------");
                    found = true;
                }
                System.out.printf("%-10s | %-5s | %-4s | %-2s | %-4s | %-6s%n",
                    ticket.getTicketId().substring(0, 8),
                    ticket.getTrainNumber(),
                    ticket.getSource(),
                    ticket.getDestination(),
                    ticket.getTravelDate(),
                    ticket.getStatus());
            }
        }
        
        if (!found) {
            System.out.println("No tickets found for this user.");
        }
    }
} 