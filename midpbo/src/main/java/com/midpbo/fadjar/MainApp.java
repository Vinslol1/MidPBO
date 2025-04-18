package com.midpbo.fadjar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private static Stage primaryStage;
    
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLoginView();
    }

    public static void showLoginView() throws Exception {
        FXMLLoader loader = new FXMLLoader(
            MainApp.class.getResource("/com/midpbo/fadjar/view/LoginView.fxml")
        );
        Parent root = loader.load();
        primaryStage.setTitle("POS System - Login");
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void showSignupView() throws Exception {
        FXMLLoader loader = new FXMLLoader(
            MainApp.class.getResource("/com/midpbo/fadjar/view/SignupView.fxml")
        );
        Parent root = loader.load();
        primaryStage.setTitle("POS System - Sign Up");
        primaryStage.setScene(new Scene(root, 400, 500));
        primaryStage.show();
    }

    public static void showPOSView() throws Exception {
        FXMLLoader loader = new FXMLLoader(
            MainApp.class.getResource("/com/midpbo/fadjar/view/POSView.fxml")
        );
        Parent root = loader.load();
        primaryStage.setTitle("POS System - Dashboard");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}