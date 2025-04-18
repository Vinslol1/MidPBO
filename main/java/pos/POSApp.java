package main.java.pos;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.pos.model.*;
import main.java.pos.view.LoginView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class POSApp extends Application {
    private Stage window;
    private Scene loginScene, mainScene;
    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<Product> products = FXCollections.observableArrayList();
    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private ObservableList<LogEntry> logs = FXCollections.observableArrayList();
    private User currentUser;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("Modern POS System");

        initializeSampleData();

        LoginView loginView = new LoginView(this);
        loginScene = loginView.createLoginScene();

        window.setScene(loginScene);
        window.setMaximized(true);
        window.show();
    }

    private void initializeSampleData() {
        users.add(new User("admin", "admin123", "Administrator", "admin@pos.com"));
        users.add(new User("cashier", "cashier123", "Cashier", "cashier@pos.com"));

        products.add(new Product("P001", "Laptop", 8000000, 10));
        products.add(new Product("P002", "Mouse", 150000, 50));
        products.add(new Product("P003", "Keyboard", 300000, 30));

        logs.add(new LogEntry("SYSTEM", "System initialized", LocalDateTime.now()));
    }

    // Getters for the data collections
    public ObservableList<User> getUsers() { return users; }
    public ObservableList<Product> getProducts() { return products; }
    public ObservableList<Transaction> getTransactions() { return transactions; }
    public ObservableList<LogEntry> getLogs() { return logs; }
    public User getCurrentUser() { return currentUser; }

    // Authentication method
    public boolean authenticate(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    // Logging method
    public void addLog(String username, String action) {
        logs.add(new LogEntry(username, action, LocalDateTime.now()));
    }

    // Scene getters and setters
    public Stage getWindow() { return window; }
    public Scene getMainScene() { return mainScene; }
    public void setMainScene(Scene mainScene) { this.mainScene = mainScene; }
}