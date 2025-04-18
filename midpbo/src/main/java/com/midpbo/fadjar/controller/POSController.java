package com.midpbo.fadjar.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import com.midpbo.fadjar.Entity.*;
import com.midpbo.fadjar.model.*;
import com.midpbo.fadjar.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class POSController {
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
    
    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private double subtotal = 0.0;
    private double tax = 0.0;
    private double total = 0.0;
    
    @FXML
    public void initialize() {
        // Initialize quantity spinner
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        
        // Initialize table columns
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        subtotalColumn.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        
        // Add remove button to action column
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");
            
            {
                removeButton.setOnAction(event -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    removeFromCart(item);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                }
            }
        });
        
        // Generate new transaction ID
        generateNewTransaction();
        
        // Enable/disable payment button based on cart content
        cartItems.addListener((ListChangeListener.Change<? extends CartItem> c) -> {
            processPaymentButton.setDisable(cartItems.isEmpty());
        });

        // Calculate change when amount paid changes
        amountPaidField.textProperty().addListener((observable, oldValue, newValue) -> {
            calculateChange();
        });
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
        product product = findProductByCode(productCode);
        
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
    
private product findProductByCode(String code) {
    // Mock implementation - in a real app, query your database
    if ("P001".equals(code)) {
        return new perishable_product(
            "P001",                      // code
            "Fresh Milk",               // name
            25000.0,                    // price (as double)
            50,                         // stock
            LocalDate.now().plusDays(30), // expired_date
            "4°C"                       // storage_temperature
        );
        
    } else if ("P002".equals(code)) {
        return new non_perishable_product(
            "P002",
            "Canned Beans",   // name
            18000,            // price
            100,              // stock
            "2025-12-31",     // expiration_date
            "Room Temperature" // storage_condition
        );
    } else if ("P003".equals(code)) {
        return new digital_product(
            "P003",
            "E-Book",         // name
            150000,           // price
            "PDF",            // format
            "LIC-12345"       // licenseKey
        );
    } else if ("P004".equals(code)) {
        List<product> products = new ArrayList<>();
        products.add(new product("P001", "Item 1", 10000, 10));
        products.add(new product("P002", "Item 2", 15000, 5));
        
        return new bundle_product(
            "P004",               // code
            "Office Suite",       // name
            0.0,                  // base price (could be dummy since getPrice is overridden)
            10,                   // stock
            products,             // included products
            15                    // bundle discount
        );
        
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
    
}