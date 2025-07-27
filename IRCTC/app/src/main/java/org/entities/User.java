package org.entities;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {
    private String name;
    private String email;
    private String hashedPassword;
    private List<String> ticketsBooked;
    private String userId;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.hashedPassword = hashPassword(password);
        this.userId = UUID.randomUUID().toString();
        this.ticketsBooked = new ArrayList<>();
    }

    // Constructor for loading existing users with already hashed password
    public User(String name, String email, String hashedPassword, String userId) {
        this.name = name;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.userId = userId;
        this.ticketsBooked = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public List<String> getBookedTickets() {
        return ticketsBooked;
    }

    public String getUserId() {
        return userId;
    }

    public Boolean checkPassword(String password) {
        String hashedInput = hashPassword(password);
        return this.hashedPassword.equals(hashedInput);
    }

    public void addTicket(String ticketId) {
        this.ticketsBooked.add(ticketId);
    }

    // Simple password hashing using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback to simple hash if SHA-256 is not available
            return String.valueOf(password.hashCode());
        }
    }
}
