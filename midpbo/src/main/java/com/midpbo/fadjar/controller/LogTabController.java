package com.midpbo.fadjar.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.midpbo.fadjar.Database_conn;

public class LogTabController {

    @FXML private ComboBox<String> userFilter;
    @FXML private DatePicker dateFilter;
    @FXML private Button filterButton;
    @FXML private Button clearFilterButton;

    @FXML private TableView<LogEntry> logTable;
    @FXML private TableColumn<LogEntry, String> userCol;
    @FXML private TableColumn<LogEntry, String> actionCol;
    @FXML private TableColumn<LogEntry, String> timestampCol;

    private final ObservableList<LogEntry> logs = FXCollections.observableArrayList();
    private final ObservableList<String> users = FXCollections.observableArrayList();


    
    @FXML
    public void initialize() {
        addLog("SYSTEM", "System initialized");
        loadLogsFromDatabase();
        // Setup columns
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        timestampCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String timestamp, boolean empty) {
                super.updateItem(timestamp, empty);
                if (empty || timestamp == null) {
                    setText(null);
                } else {
                    setText(LocalDateTime.parse(timestamp).format(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    ));
                }
            }
        });


        // Setup user filter
        userFilter.getItems().add("All Users");
        userFilter.getItems().addAll(users);
        userFilter.setValue("All Users");

        // Load logs into table
        logTable.setItems(logs);

        // Filter button action
        filterButton.setOnAction(e -> filterLogs());

        // Clear filter button
        clearFilterButton.setOnAction(e -> {
            userFilter.setValue("All Users");
            dateFilter.setValue(null);
            logTable.setItems(logs);
        });
    }

    private void filterLogs() {
        String selectedUser = userFilter.getValue();
        LocalDate date = dateFilter.getValue();

        ObservableList<LogEntry> filtered = logs.filtered(log -> {
            boolean userMatches = "All Users".equals(selectedUser) || log.getUsername().equals(selectedUser);
            boolean dateMatches = true;
            if (date != null) {
                LocalDateTime start = date.atStartOfDay();
                LocalDateTime end = start.plusDays(1);
                LocalDateTime logTime = LocalDateTime.parse(log.getTimestamp());
                dateMatches = logTime.isAfter(start) && logTime.isBefore(end);
            }
            return userMatches && dateMatches;
        });

        logTable.setItems(filtered);
    }


public void addLog(String username, String action) {
    logs.add(new LogEntry(username, action, LocalDateTime.now())); // UI side

    try (Connection conn = Database_conn.connect()) {
        String sql = "INSERT INTO logs (username, action) VALUES (?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, action);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public void loadLogsFromDatabase() {
    try (Connection conn = Database_conn.connect()) {
        String sql = "SELECT username, action, timestamp FROM logs ORDER BY timestamp DESC";
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()) {
            logs.add(new LogEntry(
                rs.getString("username"),
                rs.getString("action"),
                rs.getTimestamp("timestamp").toLocalDateTime()
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    public static class LogEntry {
        private final String username;
        private final String action;
        private final String timestamp;

        public LogEntry(String username, String action, LocalDateTime timestamp) {
            this.username = username;
            this.action = action;
            this.timestamp = timestamp.toString();
        }

        public String getUsername() { return username; }
        public String getAction() { return action; }
        public String getTimestamp() { return timestamp; }
    }
}
