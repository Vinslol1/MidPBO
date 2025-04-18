package main.java.pos.view;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import main.java.pos.POSApp;
import main.java.pos.model.Product;
import javafx.geometry.Insets;

public class ProductManagementView {
    private POSApp app;

    public ProductManagementView(POSApp app) {
        this.app = app;
    }

    public Tab createProductManagementTab() {
        Tab productTab = new Tab("Product Management");
        productTab.setClosable(false);

        // Table for products
        TableView<Product> productTable = createProductTable();

        // Form for adding/editing products
        TextField codeField = new TextField();
        codeField.setPromptText("Product Code");

        TextField nameField = new TextField();
        nameField.setPromptText("Product Name");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        TextField stockField = new TextField();
        stockField.setPromptText("Stock");

        Button addButton = new Button("Add Product");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addButton.setOnAction(e -> handleAddProduct(codeField, nameField, priceField, stockField));

        Button updateButton = new Button("Update Selected");
        updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        updateButton.setOnAction(e -> handleUpdateProduct(productTable, nameField, priceField, stockField));

        Button deleteButton = new Button("Delete Selected");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> handleDeleteProduct(productTable));

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                codeField.setText(newSelection.getCode());
                nameField.setText(newSelection.getName());
                priceField.setText(String.valueOf(newSelection.getPrice()));
                stockField.setText(String.valueOf(newSelection.getStock()));
            }
        });

        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton);

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.add(new Label("Code:"), 0, 0);
        formGrid.add(codeField, 1, 0);
        formGrid.add(new Label("Name:"), 0, 1);
        formGrid.add(nameField, 1, 1);
        formGrid.add(new Label("Price:"), 0, 2);
        formGrid.add(priceField, 1, 2);
        formGrid.add(new Label("Stock:"), 0, 3);
        formGrid.add(stockField, 1, 3);
        formGrid.add(buttonBox, 0, 4, 2, 1);

        formGrid.setPadding(new Insets(15));
        formGrid.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd;");

        HBox contentBox = new HBox(10);
        contentBox.getChildren().addAll(productTable, formGrid);
        contentBox.setPadding(new Insets(15));

        productTab.setContent(contentBox);
        return productTab;
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
        productTable.setItems(app.getProducts());
        return productTable;
    }

    private void handleAddProduct(TextField codeField, TextField nameField,
                                  TextField priceField, TextField stockField) {
        try {
            Product newProduct = new Product(
                    codeField.getText(),
                    nameField.getText(),
                    Double.parseDouble(priceField.getText()),
                    Integer.parseInt(stockField.getText())
            );
            app.getProducts().add(newProduct);
            app.addLog(app.getCurrentUser().getUsername(),
                    "Added product: " + newProduct.getCode() + " - " + newProduct.getName());
            clearFields(codeField, nameField, priceField, stockField);
        } catch (NumberFormatException ex) {
            showAlert("Invalid input", "Please enter valid numbers for price and stock");
        }
    }

    private void handleUpdateProduct(TableView<Product> productTable, TextField nameField,
                                     TextField priceField, TextField stockField) {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                selectedProduct.setName(nameField.getText());
                selectedProduct.setPrice(Double.parseDouble(priceField.getText()));
                selectedProduct.setStock(Integer.parseInt(stockField.getText()));
                productTable.refresh();
                app.addLog(app.getCurrentUser().getUsername(),
                        "Updated product: " + selectedProduct.getCode());
            } catch (NumberFormatException ex) {
                showAlert("Invalid input", "Please enter valid numbers for price and stock");
            }
        }
    }

    private void handleDeleteProduct(TableView<Product> productTable) {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            app.addLog(app.getCurrentUser().getUsername(),
                    "Deleted product: " + selectedProduct.getCode());
            app.getProducts().remove(selectedProduct);
        }
    }

    private void clearFields(Control... fields) {
        for (Control field : fields) {
            if (field instanceof TextInputControl) {
                ((TextInputControl) field).clear();
            }
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