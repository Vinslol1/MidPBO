package main.java.pos.view;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.java.pos.POSApp;
import main.java.pos.model.CartItem;
import main.java.pos.model.Product;
import javafx.geometry.Insets;
import javafx.geometry.Pos;  // Add this import
import java.text.NumberFormat;

public class SalesView {
    private POSApp app;
    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

    public SalesView(POSApp app) {
        this.app = app;
    }

    public Tab createSalesTab() {
        Tab salesTab = new Tab("Sales");
        salesTab.setClosable(false);

        // Search panel
        TextField searchField = new TextField();
        searchField.setPromptText("Search product by code or name");
        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        // Product table
        TableView<Product> productTable = createProductTable();

        // Cart table
        TableView<CartItem> cartTable = createCartTable();

        // Quantity controls
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 100, 1);

        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addToCartButton.setOnAction(e -> handleAddToCart(productTable, quantitySpinner));

        Button removeFromCartButton = new Button("Remove Selected");
        removeFromCartButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        removeFromCartButton.setOnAction(e -> handleRemoveFromCart(cartTable));

        // Payment section
        Label totalLabel = new Label("Total: Rp0");
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextField paymentField = new TextField();
        paymentField.setPromptText("Payment amount");

        Label changeLabel = new Label("Change: Rp0");

        Button processPaymentButton = new Button("Process Payment");
        processPaymentButton.setStyle("-fx-background-color: #673AB7; -fx-text-fill: white;");
        processPaymentButton.setOnAction(e -> handlePayment(paymentField, totalLabel, changeLabel));

        // Update total when cart changes
        cartItems.addListener((javafx.collections.ListChangeListener.Change<? extends CartItem> c) -> {
            double total = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
            totalLabel.setText(String.format("Total: Rp%,.2f", total));
        });

        // Search functionality
        searchButton.setOnAction(e -> handleSearch(searchField, productTable));

        // Layout
        HBox searchBox = new HBox(10, searchField, searchButton);

        HBox productControls = new HBox(10, quantitySpinner, addToCartButton);
        productControls.setAlignment(Pos.CENTER_LEFT);

        VBox productBox = new VBox(10,
                new Label("Available Products"),
                productTable,
                productControls
        );

        HBox cartControls = new HBox(10, removeFromCartButton);

        VBox cartBox = new VBox(10,
                new Label("Shopping Cart"),
                cartTable,
                cartControls,
                totalLabel,
                new Label("Payment:"),
                paymentField,
                processPaymentButton,
                changeLabel
        );

        HBox mainContent = new HBox(20, productBox, cartBox);
        mainContent.setPadding(new Insets(15));

        VBox salesLayout = new VBox(15, searchBox, mainContent);
        salesTab.setContent(salesLayout);

        // Initialize product table
        productTable.setItems(app.getProducts());

        return salesTab;
    }

    private TableView<Product> createProductTable() {
        TableView<Product> productTable = new TableView<>();

        TableColumn<Product, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setCellFactory(tc -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("Rp%,.2f", price));
                }
            }
        });

        TableColumn<Product, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));

        productTable.getColumns().addAll(codeCol, nameCol, priceCol, stockCol);
        return productTable;
    }

    private TableView<CartItem> createCartTable() {
        TableView<CartItem> cartTable = new TableView<>();

        TableColumn<CartItem, String> cartNameCol = new TableColumn<>("Product");
        cartNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<CartItem, Double> cartPriceCol = new TableColumn<>("Price");
        cartPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        cartPriceCol.setCellFactory(tc -> new TableCell<CartItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("Rp%,.2f", price));
                }
            }
        });

        TableColumn<CartItem, Integer> quantityCol = new TableColumn<>("Qty");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<CartItem, Double> subtotalCol = new TableColumn<>("Subtotal");
        subtotalCol.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        subtotalCol.setCellFactory(tc -> new TableCell<CartItem, Double>() {
            @Override
            protected void updateItem(Double subtotal, boolean empty) {
                super.updateItem(subtotal, empty);
                if (empty || subtotal == null) {
                    setText(null);
                } else {
                    setText(String.format("Rp%,.2f", subtotal));
                }
            }
        });

        cartTable.getColumns().addAll(cartNameCol, cartPriceCol, quantityCol, subtotalCol);
        cartTable.setItems(cartItems);
        return cartTable;
    }

    private void handleAddToCart(TableView<Product> productTable, Spinner<Integer> quantitySpinner) {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            int quantity = quantitySpinner.getValue();

            boolean found = false;
            for (CartItem item : cartItems) {
                if (item.getProductCode().equals(selectedProduct.getCode())) {
                    item.setQuantity(item.getQuantity() + quantity);
                    found = true;
                    break;
                }
            }

            if (!found) {
                cartItems.add(new CartItem(
                        selectedProduct.getCode(),
                        selectedProduct.getName(),
                        selectedProduct.getPrice(),
                        quantity
                ));
            }

            selectedProduct.setStock(selectedProduct.getStock() - quantity);
            productTable.refresh();
            app.addLog(app.getCurrentUser().getUsername(), "Added " + quantity + " " + selectedProduct.getName() + " to cart");
        }
    }

    private void handleRemoveFromCart(TableView<CartItem> cartTable) {
        CartItem selectedItem = cartTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            for (Product product : app.getProducts()) {
                if (product.getCode().equals(selectedItem.getProductCode())) {
                    product.setStock(product.getStock() + selectedItem.getQuantity());
                    break;
                }
            }
            app.addLog(app.getCurrentUser().getUsername(), "Removed " + selectedItem.getProductName() + " from cart");
            cartItems.remove(selectedItem);
        }
    }

    private void handlePayment(TextField paymentField, Label totalLabel, Label changeLabel) {
        try {
            double payment = Double.parseDouble(paymentField.getText());
            double total = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();

            if (payment >= total) {
                double change = payment - total;
                changeLabel.setText(String.format("Change: Rp%,.2f", change));

                app.addLog(app.getCurrentUser().getUsername(),
                        "Processed payment. Total: " + total + ", Paid: " + payment + ", Change: " + change);

                cartItems.clear();
                totalLabel.setText("Total: Rp0");
                paymentField.clear();
            } else {
                showAlert("Payment Error", "Payment amount is less than total!");
            }
        } catch (NumberFormatException ex) {
            showAlert("Invalid Input", "Please enter a valid payment amount!");
        }
    }

    private void handleSearch(TextField searchField, TableView<Product> productTable) {
        String searchTerm = searchField.getText().toLowerCase();
        if (searchTerm.isEmpty()) {
            productTable.setItems(app.getProducts());
        } else {
            ObservableList<Product> filteredProducts = app.getProducts().filtered(p ->
                    p.getCode().toLowerCase().contains(searchTerm) ||
                            p.getName().toLowerCase().contains(searchTerm)
            );
            productTable.setItems(filteredProducts);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}