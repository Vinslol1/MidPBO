package com.midpbo.fadjar.model;

public class User {
    private String username;
    private String fullName;
    private String hashedPassword;
    
    public User(String username, String fullName, String hashedPassword) {
        this.username = username;
        this.fullName = fullName;
        this.hashedPassword = hashedPassword;
    }
    
    // Getters
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getHashedPassword() { return hashedPassword; }
}