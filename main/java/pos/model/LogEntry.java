package main.java.pos.model;

import java.time.LocalDateTime;

public class LogEntry {
    private String username;
    private String action;
    private String timestamp;

    public LogEntry(String username, String action, LocalDateTime timestamp) {
        this.username = username;
        this.action = action;
        this.timestamp = timestamp.toString();
    }

    // Getters
    public String getUsername() { return username; }
    public String getAction() { return action; }
    public String getTimestamp() { return timestamp; }
}