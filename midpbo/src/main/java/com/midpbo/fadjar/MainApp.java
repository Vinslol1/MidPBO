package com.midpbo.fadjar;

import com.midpbo.fadjar.controller.POSController;
import com.midpbo.fadjar.controller.ProductTabController;
import com.midpbo.fadjar.DAO.ProductDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MainApp extends Application {
    private static MainApp instance;
    private static Stage primaryStage;
    private Connection connection;
    private ProductDAO productDAO;



    @Override
    public void start(Stage stage) throws Exception {
        instance = this;
        primaryStage = stage;
        
        // Initialize database
        String sqlFilePath = "../midpbo/QUERY.sql";
        Database_conn.runSQLFile(sqlFilePath);
        initDatabase();
        
        showLoginView();
    }

    // Proper singleton access method
    public static MainApp getInstance() {
        return instance;
    }

        private void initDatabase() throws SQLException {
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:Database.db");
                productDAO = new ProductDAO(connection);
                
                try (Statement stmt = connection.createStatement()) {
                    // Pastikan tabel products ada
                    stmt.execute("CREATE TABLE IF NOT EXISTS products (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "code TEXT NOT NULL UNIQUE, " +  // Tambahkan UNIQUE constraint
                        "name TEXT NOT NULL, " +
                        "price REAL NOT NULL, " +       // Gunakan REAL bukan FLOAT
                        "stock INTEGER NOT NULL)");
                    
                    System.out.println("Database initialized successfully"); // Debug
                }
            } catch (SQLException e) {
                System.err.println("Database initialization failed: " + e.getMessage());
                throw e;
            }
        }

    public static void showLoginView() throws IOException {
        FXMLLoader loader = new FXMLLoader(
            MainApp.class.getResource("/com/midpbo/fadjar/view/LoginView.fxml")
        );
        Parent root = loader.load();
        primaryStage.setTitle("POS System - Login");
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void showSignupView() throws IOException {
        FXMLLoader loader = new FXMLLoader(
            MainApp.class.getResource("/com/midpbo/fadjar/view/SignupView.fxml")
        );
        Parent root = loader.load();
        primaryStage.setTitle("POS System - Sign Up");
        primaryStage.setScene(new Scene(root, 400, 500));
        primaryStage.show();
    }

    public static void showPOSView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/com/midpbo/fadjar/view/POSView.fxml")
            );
            Parent root = loader.load();
            primaryStage.setTitle("POS System - Dashboard");
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.setResizable(true);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace(); // 👈 This will tell you exactly what went wrong
            System.out.println("ERROR loading POSView: " + e.getMessage());
        }
    }
    
private static void setupProductTab(TabPane tabPane, ProductDAO productDAO) throws IOException {
    FXMLLoader loader = new FXMLLoader(
        MainApp.class.getResource("/com/midpbo/fadjar/view/ProductView.fxml")
    );
    Parent productTabContent = loader.load();
    
    // Get controller and set DAO
    ProductTabController controller = loader.getController();
    controller.setProductDAO(productDAO); // Use the passed DAO
    
    Tab productTab = new Tab("Products", productTabContent);
    tabPane.getTabs().add(productTab);
}

    @Override
    public void stop() throws Exception {
        if (connection != null) {
            connection.close();
        }
        super.stop();
    }
    // Add to MainApp.java
public ProductDAO getProductDAO() {
    return this.productDAO;
}

    public static void main(String[] args) {
        launch(args);
    }

}