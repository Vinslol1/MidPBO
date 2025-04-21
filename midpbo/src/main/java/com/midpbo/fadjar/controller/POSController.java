package com.midpbo.fadjar.controller;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.midpbo.fadjar.DAO.ProductDAO;
import com.midpbo.fadjar.Database_conn;
import com.midpbo.fadjar.MainApp;
import com.midpbo.fadjar.model.*;
import com.midpbo.fadjar.util.AlertUtils;
import com.midpbo.fadjar.util.TransactionStore;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class POSController {
    private ProductDAO productDAO;
    private Connection conn;

    // UI elements
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
    @FXML private TabPane mainTabPane;


    private CartItem editingItem = null;
    private Product currentProduct;
    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private double subtotal = 0.0, tax = 0.0, total = 0.0;

    public POSController() {
        try {
            conn = Database_conn.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        // Setup spinner
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        // Setup table columns
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        subtotalColumn.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        cartTableView.setItems(cartItems);

        setupActionColumn(); // <-- new method to create Edit/Delete buttons

        // Show/hide Action column based on items in the cart
        actionColumn.setVisible(!cartItems.isEmpty());
        cartItems.addListener((ListChangeListener<CartItem>) change -> {
            actionColumn.setVisible(!cartItems.isEmpty());
        });

        // Inject DAO and init tabs
        productDAO = MainApp.getInstance().getProductDAO();
        initializeProductTab(productDAO);
        initializeTransactionTab();
        initializeLogTab();
        generateNewTransaction();

        amountPaidField.textProperty().addListener((obs, oldVal, newVal) -> {
            calculateChange();
        });        
    }

    

    private void initializeProductTab(ProductDAO productDAO) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/midpbo/fadjar/view/ProductTab.fxml"));
            Parent productTabContent = loader.load();
            ProductTabController controller = loader.getController();
            controller.setProductDAO(productDAO);

            Tab productTab = new Tab("Product Management", productTabContent);
            productTab.setClosable(false);
            mainTabPane.getTabs().add(productTab);
        } catch (IOException e) {
            showLoadError("Product Tab", e);
        }
    }

    private void initializeTransactionTab() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/midpbo/fadjar/view/TransactionTab.fxml"));
            Parent transactionTabContent = loader.load();

            Tab transactionTab = new Tab("Transaction History", transactionTabContent);
            transactionTab.setClosable(false);
            mainTabPane.getTabs().add(transactionTab);
        } catch (IOException e) {
            showLoadError("Transaction Tab", e);
        }
    }

    private void initializeLogTab() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/midpbo/fadjar/view/LogTab.fxml"));
            Parent logTabContent = loader.load();

            Tab logTab = new Tab("Log Report", logTabContent);
            logTab.setClosable(false);
            mainTabPane.getTabs().add(logTab);
        } catch (IOException e) {
            showLoadError("Log Tab", e);
        }
    }

    private void showLoadError(String tabName, Exception e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Failed to load " + tabName);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    private void setupActionColumn() {
    actionColumn.setCellFactory(col -> new TableCell<>() {
        private final Button editButton = new Button("Edit");
        private final Button deleteButton = new Button("Delete");
        private final HBox hbox = new HBox(5, editButton, deleteButton);

        {
            hbox.setAlignment(Pos.CENTER);
            editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
            deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");

            editButton.setOnAction(e -> {
                CartItem item = getTableView().getItems().get(getIndex());
                quantitySpinner.getValueFactory().setValue(item.getQuantity());
                editingItem = item;
                addToCartButton.setText("Update Item");
                addToCartButton.setDisable(false); // ✅ ENABLE it here
            });
            
            
            deleteButton.setOnAction(e -> {
                CartItem item = getTableView().getItems().get(getIndex());
                cartItems.remove(item);
                updateTotals(); // ✅ recalculate after deletion
            });            
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : hbox);
        }
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
    
        Product product = findProductByCode(productCode);
    
        if (product != null) {
            currentProduct = product; // ✅ Save selected product
    
            productCodeLabel.setText(product.getCode());
            productNameLabel.setText(product.getName());
            productPriceLabel.setText(String.format("%,.2f", product.getPrice()));
            productStockLabel.setText(String.valueOf(product.getStock()));
    
            // Enable Add to Cart now that product is valid
            addToCartButton.setDisable(false);
    
            // Setup quantity spinner with max = stock
            SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, product.getStock(), 1);
            quantitySpinner.setValueFactory(valueFactory);
        } else {
            AlertUtils.showError("Not Found", "Product not found with code: " + productCode);
            resetProductDetails(); // Will also disable the button
        }
    }


    private Product findProductByCode(String code) {
        String query = "SELECT * FROM products WHERE code = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Product(
                    rs.getInt("id"),
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void resetProductDetails() {
        productCodeLabel.setText("");
        productNameLabel.setText("");
        productPriceLabel.setText("");
        productStockLabel.setText("");
        currentProduct = null; // ✅ clear product reference
        addToCartButton.setDisable(true);
    }    

    @FXML
    private void handleAddToCart() {
        int quantity = quantitySpinner.getValue();
    
        if (editingItem != null) {
            // Edit mode: update the quantity directly
            editingItem.setQuantity(quantity);
            cartTableView.refresh();
            updateTotals();
            editingItem = null; // Exit edit mode
            addToCartButton.setText("Add to Cart");
            productCodeField.clear();
            resetProductDetails();
            return;
        }
    
        // Normal add-to-cart mode
        if (currentProduct == null) {
            AlertUtils.showError("No Product", "Please search and select a product first.");
            return;
        }
    
        String code = currentProduct.getCode();
        String name = currentProduct.getName();
        double price = currentProduct.getPrice();
        
        for (CartItem item : cartItems) {
            if (item.getProductCode().equals(code)) {
                item.setQuantity(item.getQuantity() + quantity);
                cartTableView.refresh();
                updateTotals();
                return;
            }
        }
    
        CartItem newItem = new CartItem(code, name, price, quantity);
        cartItems.add(newItem);
        updateTotals();
    
        productCodeField.clear();
        resetProductDetails();
    }    

    private void updateTotals() {
        subtotal = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
        tax = subtotal * 0.1;
        total = subtotal + tax;

        subtotalLabel.setText(String.format("%,.2f", subtotal));
        taxLabel.setText(String.format("%,.2f", tax));
        totalLabel.setText(String.format("%,.2f", total));
        calculateChange();
    }

    private void calculateChange() {
        try {
            double amountPaid = Double.parseDouble(amountPaidField.getText().replace(",", ""));
            double change = amountPaid - total;
    
            if (change >= 0) {
                changeLabel.setText(String.format("%,.2f", change));
                processPaymentButton.setDisable(false); // ✅ Enable when valid
            } else {
                changeLabel.setText("0.00");
                processPaymentButton.setDisable(true); // ❌ Not enough money
            }
        } catch (NumberFormatException e) {
            changeLabel.setText("0.00");
            processPaymentButton.setDisable(true); // ❌ Invalid input
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
            String transactionId = transactionIdLabel.getText();
            LocalDateTime now = LocalDateTime.now();
            String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    
            // Store in memory (TransactionStore)
            TransactionStore.add(new TransactionRecord(
                transactionId,
                formattedDate,
                transactionType,
                total
            ));
    
            // Insert into database
            String insertTxnSQL = "INSERT INTO transactions (id, date, type, total) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertTxnSQL)) {
                pstmt.setString(1, transactionId);
                pstmt.setString(2, formattedDate);
                pstmt.setString(3, transactionType);
                pstmt.setDouble(4, total);
                pstmt.executeUpdate();
            }
    
            // Insert each cart item into transaction_items
            String insertItemSQL = "INSERT INTO transaction_items (transaction_id, product_code, quantity, price_per_unit) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertItemSQL)) {
                for (CartItem item : cartItems) {
                    pstmt.setString(1, transactionId);
                    pstmt.setString(2, item.getCode());
                    pstmt.setInt(3, item.getQuantity());
                    pstmt.setDouble(4, item.getPrice());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
    
            // Transaction logic
            transaction transaction;
            if ("PURCHASE".equals(transactionType)) {
                transaction = new PurchaseTransaction(transactionId, now, cartItems);
            } else {
                transaction = new RefundTransaction(transactionId, now, cartItems);
            }
    
            transaction.processTransaction();
            transaction.serializeTransaction();
    
            AlertUtils.showInfo("Success", "Transaction processed successfully\n" +
                "Total: " + totalLabel.getText() + "\n" +
                "Change: " + changeLabel.getText());
    
            handleNewTransaction();
    
        } catch (NumberFormatException e) {
            AlertUtils.showError("Error", "Please enter a valid payment amount");
        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtils.showError("Database Error", "Failed to store transaction: " + e.getMessage());
        }
    }    

    @FXML
    private void handleNewTransaction() {
        cartItems.clear();
        subtotal = tax = total = 0.0;
        subtotalLabel.setText("0.00");
        taxLabel.setText("0.00");
        totalLabel.setText("0.00");
        amountPaidField.clear();
        changeLabel.setText("0.00");
        productCodeField.clear();
        resetProductDetails();
        generateNewTransaction();
        statusLabel.setText("New transaction started");
    }
}
