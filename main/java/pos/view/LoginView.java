package main.java.pos.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.java.pos.POSApp;
import javafx.geometry.Insets;

public class LoginView {
    private POSApp app;

    public LoginView(POSApp app) {
        this.app = app;
    }

    public Scene createLoginScene() {
        // Logo
        ImageView logo = new ImageView(new Image("https://via.placeholder.com/150"));
        logo.setFitWidth(100);
        logo.setFitHeight(100);

        // Login Form
        Label titleLabel = new Label("POS SYSTEM LOGIN");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);

        Label userLabel = new Label("Username:");
        TextField userField = new TextField();
        userField.setPromptText("Enter username");

        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter password");

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        loginButton.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();

            if (app.authenticate(username, password)) {
                app.addLog(app.getCurrentUser().getUsername(), "User logged in");
                app.setMainScene(new MainView(app).createMainScene());
                app.getWindow().setScene(app.getMainScene());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText(null);
                alert.setContentText("Invalid username or password!");
                alert.showAndWait();
            }
        });

        VBox formLayout = new VBox(10);
        formLayout.getChildren().addAll(titleLabel, userLabel, userField, passLabel, passField, loginButton);
        formLayout.setAlignment(Pos.CENTER);
        formLayout.setPadding(new Insets(20));
        formLayout.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5;");

        VBox loginLayout = new VBox(20);
        loginLayout.getChildren().addAll(logo, formLayout);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #e6f7ff, #ffffff);");

        return new Scene(loginLayout, 800, 600);
    }
}