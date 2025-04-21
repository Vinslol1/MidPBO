package com.midpbo.fadjar.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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

        // Sample data
        initializeSampleData();

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

    private void initializeSampleData() {
        addLog("SYSTEM", "System initialized");
        addLog("admin", "Logged in");
        addLog("admin", "Added new product");
        addLog("staff", "Processed transaction");
    }

    public void addLog(String username, String action) {
        logs.add(new LogEntry(username, action, LocalDateTime.now()));
        if (!users.contains(username)) {
            users.add(username);
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
