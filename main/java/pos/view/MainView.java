package main.java.pos.view;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import main.java.pos.POSApp;
import javafx.geometry.Insets;

public class MainView {
    private POSApp app;

    public MainView(POSApp app) {
        this.app = app;
    }

    public Scene createMainScene() {
        TabPane tabPane = new TabPane();

        // Create tabs
        UserManagementView userManagementView = new UserManagementView(app);
        SalesView salesView = new SalesView(app);
        ProductManagementView productManagementView = new ProductManagementView(app);
        LogsView logsView = new LogsView(app);

        tabPane.getTabs().addAll(
                userManagementView.createUserManagementTab(),
                salesView.createSalesTab(),
                productManagementView.createProductManagementTab(),
                logsView.createLogsTab()
        );

        // Main layout with header
        BorderPane mainLayout = new BorderPane();

        // Header
        HBox header = new HBox();
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #3f51b5;");

        Label welcomeLabel = new Label("Welcome, " + (app.getCurrentUser() != null ? app.getCurrentUser().getFullName() : "Guest"));
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> {
            app.addLog(app.getCurrentUser().getUsername(), "User logged out");
            app.getWindow().setScene(new LoginView(app).createLoginScene());
        });

        HBox.setHgrow(welcomeLabel, Priority.ALWAYS);
        header.getChildren().addAll(welcomeLabel, logoutButton);

        mainLayout.setTop(header);
        mainLayout.setCenter(tabPane);

        return new Scene(mainLayout);
    }
}