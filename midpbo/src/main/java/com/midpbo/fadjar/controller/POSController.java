package com.midpbo.fadjar.controller;

import java.io.IOException;
import java.sql.*;
import com.midpbo.fadjar.DAO.ProductDAO;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import com.midpbo.fadjar.model.*;
import com.midpbo.fadjar.util.*;
import com.midpbo.fadjar.Database_conn;
import com.midpbo.fadjar.MainApp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class POSController {
    private ProductDAO productDAO; 
    
    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }    
    // FXML injected fields
    @FXML private TextField productCodeField;
    @FXML private Label productCodeLabel, productNameLabel, productPriceLabel, productStockLabel;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Button addToCartButton;
    @FXML private TableView<CartItem> cartTableView;
    @FXML private TableColumn<CartItem, String> codeColumn, nameColumn;
    @FXML private TableColumn<CartItem, Double> priceColumn, subtotalColumn;
    @FXML private TableColumn<CartItem, Integer> quantityColumn;
    @FXML private TableColumn<CartItem, Void> actionColumn;
    @FXML private Label transactionIdLabel, transactionDateLabel;
    @FXML private Label subtotalLabel, taxLabel, totalLabel;
    @FXML private TextField amountPaidField;
    @FXML private Label changeLabel, statusLabel;
    @FXML private Button processPaymentButton;
    @FXML private ToggleGroup transactionTypeGroup;
    @FXML private TabPane mainTabPane; // This must match the fx:id in FXML

    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private double subtotal = 0.0;
    private double tax = 0.0;
    private double total = 0.0;

    private Connection conn;

    // Database connection
    public POSController() {
        try {
            conn = Database_conn.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    
// In POSController.java
@FXML
private void initialize() {
    // Other initialization code...
    
    // Get the ProductDAO from MainApp instance
    ProductDAO productDAO = MainApp.getInstance().getProductDAO();
    
    // Initialize product tab with this DAO
    initializeProductTab(productDAO);
    initializeTransactionTab();
    initializeLogTab();
}

private void initializeProductTab(ProductDAO productDAO) {
    try {
        // 1. Load the FXML content
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/midpbo/fadjar/view/ProductTab.fxml")
        );
        Parent productTabContent = loader.load();
        
        // 2. Get the controller and set the DAO
        ProductTabController controller = loader.getController();
        controller.setProductDAO(productDAO);
        
        // 3. Create a new Tab and set its content
        Tab productTab = new Tab("Product Management", productTabContent);
        productTab.setClosable(false); // Optional: prevent closing
        
        // 4. Add the tab to the TabPane (assuming you have @FXML TabPane)
        mainTabPane.getTabs().add(productTab);
        
    } catch (IOException e) {
        e.printStackTrace();
        // Consider showing an error alert to the user
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Failed to load product tab");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}

private void initializeTransactionTab() {
    try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/midpbo/fadjar/view/TransactionTab.fxml")
        );
        Parent transactionTabContent = loader.load();
        
        Tab transactionTab = new Tab("Transaction History", transactionTabContent);
        transactionTab.setClosable(false); 
        
        mainTabPane.getTabs().add(transactionTab);
        
    } catch (IOException e) {
        e.printStackTrace();
        // Consider showing an error alert to the user
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Failed to load transaction tab");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}
    
private void initializeLogTab() {
    try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/midpbo/fadjar/view/LogTab.fxml")
        );
        Parent logTabContent = loader.load();
        
        Tab logTab = new Tab("Log Report", logTabContent);
        logTab.setClosable(false); 
        
        mainTabPane.getTabs().add(logTab);
        
    } catch (IOException e) {
        e.printStackTrace();
        // Consider showing an error alert to the user
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Failed to load Log tab");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}

    private void generateNewTransaction() {
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        transactionIdLabel.setText(transactionId);
        transactionDateLabel.setText(currentDate);
    }
    
    @FXML
    private void handleSearchProduct() {
        String productCode = productCodeField.getText().trim();
        if (productCode.isEmpty()) {
            AlertUtils.showError("Error", "Please enter a product code");
            return;
        }
        
        // In a real application, you would search your database here
        // This is a mock implementation
        Product product = findProductByCode(productCode);
        
        if (product != null) {
            productCodeLabel.setText(product.getCode());
            productNameLabel.setText(product.getName());
            productPriceLabel.setText(String.format("%,.2f", product.getPrice()));
            productStockLabel.setText(String.valueOf(product.getStock()));
            addToCartButton.setDisable(false);
            
            // Set max value for spinner based on stock
            quantitySpinner.getValueFactory().setValue(1);

// With this:
            SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = 
            (SpinnerValueFactory.IntegerSpinnerValueFactory) quantitySpinner.getValueFactory();
            valueFactory.setMax(product.getStock());
        } else {
            AlertUtils.showError("Not Found", "Product not found with code: " + productCode);
            resetProductDetails();
        }
    }
    
    private Product findProductByCode(String code) {
        String query = "SELECT * FROM products WHERE code = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("id"); // Get the ID
                String productCode = rs.getString("code");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");
                
                return new Product(id, productCode, name, price, stock);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

}
    
    private void resetProductDetails() {
        productCodeLabel.setText("-");
        productNameLabel.setText("-");
        productPriceLabel.setText("-");
        productStockLabel.setText("-");
        addToCartButton.setDisable(true);
    }
    
    @FXML
    private void handleAddToCart() {
        String productCode = productCodeLabel.getText();
        String productName = productNameLabel.getText();
        double price = Double.parseDouble(productPriceLabel.getText().replace(",", ""));
        int quantity = quantitySpinner.getValue();
        
        // Check if product already in cart
        for (CartItem item : cartTableView.getItems()) {
            if (item.getProductCode().equals(productCode)) {
                item.setQuantity(item.getQuantity() + quantity);
                cartTableView.refresh();
                updateTotals();
                return;
            }
        }
        
        // Add new item to cart
        CartItem newItem = new CartItem(productCode, productName, price, quantity);
        cartTableView.getItems().add(newItem);
        updateTotals();
        
        // Reset product search
        productCodeField.clear();
        resetProductDetails();
    }
    
    private void removeFromCart(CartItem item) {
        cartTableView.getItems().remove(item);
        updateTotals();
    }
    
    private void updateTotals() {
        subtotal = cartTableView.getItems().stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
        tax = subtotal * 0.1; // Assuming 10% tax
        total = subtotal + tax;
        
        subtotalLabel.setText(String.format("%,.2f", subtotal));
        taxLabel.setText(String.format("%,.2f", tax));
        totalLabel.setText(String.format("%,.2f", total));
        
        // Re-calculate change if amount paid was already entered
        calculateChange();
    }
    
    private void calculateChange() {
        try {
            double amountPaid = Double.parseDouble(amountPaidField.getText());
            double change = amountPaid - total;
            changeLabel.setText(String.format("%,.2f", change));
        } catch (NumberFormatException e) {
            changeLabel.setText("0.00");
        }
    }
    
    @FXML
    private void handleProcessPayment() {
        try {
            double amountPaid = Double.parseDouble(amountPaidField.getText());
            if (amountPaid < total) {
                AlertUtils.showError("Payment Error", "Amount paid is less than total amount");
                return;
            }
            
            String transactionType = (String) transactionTypeGroup.getSelectedToggle().getUserData();
            transaction transaction;
            
            if ("PURCHASE".equals(transactionType)) {
                transaction = new PurchaseTransaction(
                    transactionIdLabel.getText(),
                    LocalDateTime.now(),
                    cartTableView.getItems()
                );
            } else {
                transaction = new RefundTransaction(
                    transactionIdLabel.getText(),
                    LocalDateTime.now(),
                    cartTableView.getItems()
                );
            }
            
            // Process the transaction
            transaction.processTransaction();
            transaction.serializeTransaction();
            
            // Show success message
            AlertUtils.showInfo("Success", "Transaction processed successfully\n" +
                "Total: " + totalLabel.getText() + "\n" +
                "Change: " + changeLabel.getText());
            
            // Reset for new transaction
            handleNewTransaction();
        } catch (NumberFormatException e) {
            AlertUtils.showError("Error", "Please enter a valid payment amount");
        }
    }
    
    @FXML
    private void handleNewTransaction() {
        // Clear cart
        cartTableView.getItems().clear();
        
        // Reset totals
        subtotal = 0.0;
        tax = 0.0;
        total = 0.0;
        subtotalLabel.setText("0.00");
        taxLabel.setText("0.00");
        totalLabel.setText("0.00");
        
        // Clear payment fields
        amountPaidField.clear();
        changeLabel.setText("0.00");
        
        // Generate new transaction ID
        generateNewTransaction();
        
        // Reset product search
        productCodeField.clear();
        resetProductDetails();
        
        statusLabel.setText("New transaction started");
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
}