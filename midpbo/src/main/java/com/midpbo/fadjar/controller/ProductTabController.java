package com.midpbo.fadjar.controller;

import com.midpbo.fadjar.model.Product;
import com.midpbo.fadjar.DAO.ProductDAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;

public class ProductTabController {
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> idColumn;
    @FXML private TableColumn<Product, String> codeColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, Double> priceColumn;
    @FXML private TableColumn<Product, Integer> stockColumn;
    
    @FXML private TextField codeField;
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    @FXML private TabPane mainTabPane; 

    
    private final ObservableList<Product> productData = FXCollections.observableArrayList();
    private ProductDAO productDAO;

    @FXML
    public void initialize() {
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        
        // Listen for selection changes
        productTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> showProductDetails(newSelection));
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
        refreshTable();
    }

    private void refreshTable() {
        productData.clear();
        productData.addAll(productDAO.getAllProducts());
        productTable.setItems(productData);
        System.out.println("Table refreshed. Items: " + productData.size());
    }

    private void showProductDetails(Product product) {
        if (product != null) {
            codeField.setText(product.getCode());
            nameField.setText(product.getName());
            priceField.setText(String.valueOf(product.getPrice()));
            stockField.setText(String.valueOf(product.getStock()));
        } else {
            clearFields();
        }
    }

    @FXML
    private void handleAddProduct() {
        if (validateInput()) {
            try {
                Product product = new Product(
                    0,
                    codeField.getText(),
                    nameField.getText(),
                    Double.parseDouble(priceField.getText()),
                    Integer.parseInt(stockField.getText())
                );
                
                productDAO.addProduct(product);
                refreshTable();
                clearFields();
            } catch (Exception e) {
                showAlert("Error", "Failed to add product: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleUpdateProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null && validateInput()) {
            try {
                selectedProduct.setCode(codeField.getText());
                selectedProduct.setName(nameField.getText());
                selectedProduct.setPrice(Double.parseDouble(priceField.getText()));
                selectedProduct.setStock(Integer.parseInt(stockField.getText()));
                
                productDAO.updateProduct(selectedProduct);
                refreshTable();
            } catch (Exception e) {
                showAlert("Error", "Failed to update product: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleDeleteProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                productDAO.deleteProduct(selectedProduct.getId());
                refreshTable();
                clearFields();
            } catch (Exception e) {
                showAlert("Error", "Failed to delete product: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRefresh() {
        refreshTable();
    }

    private boolean validateInput() {
        String errorMessage = "";
        
        if (codeField.getText() == null || codeField.getText().isEmpty()) {
            errorMessage += "Code is required!\n";
        }
        
        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            errorMessage += "Name is required!\n";
        }
        
        try {
            Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            errorMessage += "Price must be a number!\n";
        }
        
        try {
            Integer.parseInt(stockField.getText());
        } catch (NumberFormatException e) {
            errorMessage += "Stock must be an integer!\n";
        }
        
        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert("Invalid Input", errorMessage);
            return false;
        }
    }

    private void clearFields() {
        codeField.clear();
        nameField.clear();
        priceField.clear();
        stockField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}