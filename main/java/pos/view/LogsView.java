package main.java.pos.view;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import main.java.pos.POSApp;
import main.java.pos.model.LogEntry;
import javafx.geometry.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogsView {
    private POSApp app;

    public LogsView(POSApp app) {
        this.app = app;
    }

    public Tab createLogsTab() {
        Tab logTab = new Tab("Logs");
        logTab.setClosable(false);

        // Table for logs
        TableView<LogEntry> logTable = createLogTable();

        // Filter controls
        ComboBox<String> userFilter = new ComboBox<>();
        userFilter.getItems().add("All Users");
        app.getUsers().forEach(user -> userFilter.getItems().add(user.getUsername()));
        userFilter.setValue("All Users");

        DatePicker dateFilter = new DatePicker();
        dateFilter.setPromptText("Filter by date");

        Button filterButton = new Button("Filter");
        filterButton.setOnAction(e -> applyFilters(logTable, userFilter, dateFilter));

        Button clearFilterButton = new Button("Clear");
        clearFilterButton.setOnAction(e -> {
            userFilter.setValue("All Users");
            dateFilter.setValue(null);
            logTable.setItems(app.getLogs());
        });

        HBox filterBox = new HBox(10,
                new Label("Filter by:"),
                userFilter,
                dateFilter,
                filterButton,
                clearFilterButton
        );
        filterBox.setPadding(new Insets(10));

        VBox logLayout = new VBox(10, filterBox, logTable);
        logLayout.setPadding(new Insets(15));

        logTab.setContent(logLayout);
        return logTab;
    }

    private TableView<LogEntry> createLogTable() {
        TableView<LogEntry> logTable = new TableView<>();

        TableColumn<LogEntry, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<LogEntry, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));

        TableColumn<LogEntry, String> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        timestampCol.setCellFactory(tc -> new TableCell<LogEntry, String>() {
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

        logTable.getColumns().addAll(userCol, actionCol, timestampCol);
        logTable.setItems(app.getLogs());
        return logTable;
    }

    private void applyFilters(TableView<LogEntry> logTable, ComboBox<String> userFilter, DatePicker dateFilter) {
        String selectedUser = userFilter.getValue();
        if (dateFilter.getValue() != null) {
            LocalDateTime start = dateFilter.getValue().atStartOfDay();
            LocalDateTime end = start.plusDays(1);

            if ("All Users".equals(selectedUser)) {
                logTable.setItems(app.getLogs().filtered(log ->
                        LocalDateTime.parse(log.getTimestamp()).isAfter(start) &&
                                LocalDateTime.parse(log.getTimestamp()).isBefore(end)
                ));
            } else {
                logTable.setItems(app.getLogs().filtered(log ->
                        log.getUsername().equals(selectedUser) &&
                                LocalDateTime.parse(log.getTimestamp()).isAfter(start) &&
                                LocalDateTime.parse(log.getTimestamp()).isBefore(end)
                ));
            }
        } else {
            if ("All Users".equals(selectedUser)) {
                logTable.setItems(app.getLogs());
            } else {
                logTable.setItems(app.getLogs().filtered(log ->
                        log.getUsername().equals(selectedUser)
                ));
            }
        }
    }
}