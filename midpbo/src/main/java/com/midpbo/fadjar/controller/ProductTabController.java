package com.midpbo.fadjar.controller;

import com.midpbo.fadjar.model.Product;

import java.util.ArrayList;
import java.time.LocalDate;  

import com.midpbo.fadjar.DAO.ProductDAO;
import com.midpbo.fadjar.model.PerishableProduct;
import com.midpbo.fadjar.model.DigitalProduct;
import com.midpbo.fadjar.model.BundleProduct;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;

public class ProductTabController {
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> idColumn;
    @FXML private TableColumn<Product, String> codeColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, Double> priceColumn;
    @FXML private TableColumn<Product, Integer> stockColumn;
    @FXML private TableColumn<Product, String> expiryDateColumn;
    @FXML private TableColumn<Product, String> TypeColumn;
    @FXML private TableColumn<Product, String> descriptionColumn;
    @FXML private TableColumn<Product, String> URLColumn;



    
    @FXML private TextField codeField;
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    @FXML private TextField urlField;    
    @FXML private TextField bundleNameField;
    @FXML private TextField bundlePriceField;
    @FXML private TextField bundleDiscountField;
    @FXML private TextField bundleDescriptionField;
    @FXML private TextField descriptionField;

    
    @FXML private VBox perishableFields;
    @FXML private VBox digitalFields;
    @FXML private VBox bundleFields;
    @FXML private DatePicker expiryDatePicker;
    @FXML private ListView<Product> selectedProductsListView;
    @FXML private TabPane mainTabPane; 
    @FXML private ComboBox<String> typeComboBox; 

    
    private final ObservableList<Product> productData = FXCollections.observableArrayList();
    private ProductDAO productDAO;

    @FXML
    public void initialize() {
        // Initialize product type combobox
        ObservableList<String> productTypes = FXCollections.observableArrayList(
            "Regular", "Perishable", "Digital", "Bundle"
        );
        typeComboBox.setItems(productTypes);
        typeComboBox.setValue("Regular"); // Set default value
        
        // Add listener to show/hide type-specific fields
        typeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            updateTypeSpecificFields(newValue);
        });

        // Initialize the expiry date column
        expiryDateColumn.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            if (product instanceof PerishableProduct) {
                PerishableProduct perishable = (PerishableProduct) product;
                LocalDate expiryDate = perishable.getExpiredDate();
                return new SimpleStringProperty(expiryDate != null ? expiryDate.toString() : "");
            }
            return new SimpleStringProperty("");
        });

        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        expiryDateColumn.setCellValueFactory(new PropertyValueFactory<>("expiredDate"));
        URLColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Listen for selection changes
        productTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> showProductDetails(newSelection));
    }
    
    private void updateTypeSpecificFields(String productType) {
        // Hide all type-specific fields
        perishableFields.setVisible(false);
        perishableFields.setManaged(false);
        digitalFields.setVisible(false);
        digitalFields.setManaged(false);
        bundleFields.setVisible(false);
        bundleFields.setManaged(false);
        
        // Show fields for selected product type
        switch (productType) {
            case "Perishable":
                perishableFields.setVisible(true);
                perishableFields.setManaged(true);
                break;
            case "Digital":
                digitalFields.setVisible(true);
                digitalFields.setManaged(true);
                break;
            case "Bundle":
                bundleFields.setVisible(true);
                bundleFields.setManaged(true);
                break;
        }
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
        refreshTable();
    }
    private void refreshTable() {
        productData.clear();
        
        try {
            // Add regular products
            productData.addAll(productDAO.getAllProducts());
        } catch (Exception e) {
            System.err.println("Error loading regular products: " + e.getMessage());
            e.printStackTrace();
        }
        
        try {
            // Add perishable products
            productData.addAll(productDAO.getAllPerishableProducts());
        } catch (Exception e) {
            System.err.println("Error loading perishable products: " + e.getMessage());
            e.printStackTrace();
        }
        
        try {
            // Add digital products
            productData.addAll(productDAO.getAllDigitalProducts());
        } catch (Exception e) {
            System.err.println("Error loading digital products: " + e.getMessage());
            e.printStackTrace();
        }
        

        
        productTable.setItems(productData);
        System.out.println("Table refreshed. Total items: " + productData.size());
    }

    private void showProductDetails(Product product) {
        if (product != null) {
            codeField.setText(product.getCode());
            nameField.setText(product.getName());
            priceField.setText(String.valueOf(product.getPrice()));
            stockField.setText(String.valueOf(product.getStock()));            
            // Handle type-specific fields
            if (product instanceof PerishableProduct) {
                PerishableProduct perishable = (PerishableProduct) product;
                expiryDatePicker.setValue(perishable.getExpiredDate());
                typeComboBox.setValue("Perishable");
            } else if (product instanceof DigitalProduct) {
                DigitalProduct digital = (DigitalProduct) product;
                urlField.setText(digital.getUrl());
                typeComboBox.setValue("Digital");
                descriptionField.setText(digital.getDescription());
            } else {
                typeComboBox.setValue("Regular");
            }
        } else {
            clearFields();
        }
    }
    
    @FXML
    private void handleAddProduct() {  // Remove parameter
        String productType = typeComboBox.getValue(); // Get type from UI component
        
        if (productType.equals("Perishable")) {
            // Your perishable product handling code
        }
        if (validateInput()) {
            try {
                if (productType.equals("Regular")) {
                    Product product = new Product(
                        0,
                        codeField.getText(),
                        nameField.getText(),
                        Double.parseDouble(priceField.getText()),
                        Integer.parseInt(stockField.getText())
                    );
                    
                    productDAO.addProduct(product);
                } 
                else if (productType.equals("Perishable")) {
                    PerishableProduct product = new PerishableProduct(
                        0,  // ID tidak digunakan dalam query saat ini
                        codeField.getText(),
                        nameField.getText(),
                        Double.parseDouble(priceField.getText()),
                        Integer.parseInt(stockField.getText()),
                        expiryDatePicker.getValue()
                    );
                    
                    productDAO.addPerishableProduct(product);
                }
                else if (productType.equals("Digital")) {
                    DigitalProduct product = new DigitalProduct(
                        0,
                        codeField.getText(),
                        nameField.getText(),
                        Double.parseDouble(priceField.getText()),
                        urlField.getText(),
                        descriptionField.getText()
                    );
                    
                    productDAO.addDigitalProduct(product);
                }
                else if (productType.equals("Bundle")) {
                    BundleProduct bundle = new BundleProduct(
                        0,
                        bundleNameField.getText(),
                        Double.parseDouble(bundlePriceField.getText()),
                        Integer.parseInt(bundleDiscountField.getText()),
                        bundleDescriptionField.getText()
                    );
                    
                    bundle.setItems(FXCollections.observableArrayList());
                    
                    productDAO.addBundleProduct(bundle);
                }
                
                refreshTable();
                clearFields();
            } catch (Exception e) {
                showAlert("Error", "Failed to add product: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleUpdateProduct() {
        Object selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null && validateInput()) {
            try {
                String productType = getProductType(selectedProduct);
                
                if (productType.equals("Regular")) {
                    Product product = (Product) selectedProduct;
                    product.setCode(codeField.getText());
                    product.setName(nameField.getText());
                    product.setPrice(Double.parseDouble(priceField.getText()));
                    product.setStock(Integer.parseInt(stockField.getText()));
                    
                    productDAO.updateProduct(product);
                }
                else if (productType.equals("Perishable")) {
                    PerishableProduct product = (PerishableProduct) selectedProduct;
                    product.setCode(codeField.getText());
                    product.setName(nameField.getText());
                    product.setPrice(Double.parseDouble(priceField.getText()));
                    product.setStock(Integer.parseInt(stockField.getText()));
                    product.setExpiredDate(expiryDatePicker.getValue());
                    
                    productDAO.updatePerishableProduct(product);
                }
                else if (productType.equals("Digital")) {
                    DigitalProduct product = (DigitalProduct) selectedProduct;
                    product.setCode(codeField.getText());
                    product.setName(nameField.getText());
                    product.setPrice(Double.parseDouble(priceField.getText()));
                    product.setStock(Integer.parseInt(stockField.getText()));
                    product.setUrl(urlField.getText());
                    
                    productDAO.updateDigitalProduct(product);
                }
                else if (productType.equals("Bundle")) {
                    BundleProduct bundle = (BundleProduct) selectedProduct;
                    bundle.setBundleName(bundleNameField.getText());
                    bundle.setBundlePrice(Double.parseDouble(bundlePriceField.getText()));
                    bundle.setBundleDiscount(Integer.parseInt(bundleDiscountField.getText()));
                    bundle.setBundleDescription(bundleDescriptionField.getText());
                    bundle.setItems(FXCollections.observableArrayList());
                    
                    productDAO.updateBundleProduct(bundle);
                }
                
                refreshTable();
            } catch (Exception e) {
                showAlert("Error", "Failed to update product: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleDeleteProduct() {
        Object selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                String productType = getProductType(selectedProduct);
                
                if (productType.equals("Regular")) {
                    productDAO.deleteProduct(((Product) selectedProduct).getId());
                }
                else if (productType.equals("Perishable")) {
                    productDAO.deletePerishableProduct(((PerishableProduct) selectedProduct).getId());
                }
                else if (productType.equals("Digital")) {
                    productDAO.deleteDigitalProduct(((DigitalProduct) selectedProduct).getId());
                }
                else if (productType.equals("Bundle")) {
                    productDAO.deleteBundleProduct(((BundleProduct) selectedProduct).getId());
                }
                
                refreshTable();
                clearFields();
            } catch (Exception e) {
                showAlert("Error", "Failed to delete product: " + e.getMessage());
            }
        }
    }
    
    // Helper method to determine product type
    private String getProductType(Object product) {
        if (product instanceof PerishableProduct) {
            return "Perishable";
        } else if (product instanceof DigitalProduct) {
            return "Digital";
        } else if (product instanceof BundleProduct) {
            return "Bundle";
        } else {
            return "Regular";
        }
    }
    
    @FXML
    private void handleRefresh() {
        refreshTable();
    }

    private boolean validateInput() {
        String errorMessage = "";
        String productType = typeComboBox.getValue();
        
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
        

        
        // Validate type-specific fields
        if (productType.equals("Perishable")) {
            if (expiryDatePicker.getValue() == null) {
                errorMessage += "Expiry date is required for perishable products!\n";
            }
        } else if (productType.equals("Digital")) {
            if (urlField.getText() == null || urlField.getText().isEmpty()) {
                errorMessage += "URL is required for digital products!\n";
            }
        } else if (productType.equals("Bundle")) {
            if (bundleNameField.getText() == null || bundleNameField.getText().isEmpty()) {
                errorMessage += "Bundle name is required!\n";
            }
            
            try {
                Double.parseDouble(bundlePriceField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Bundle price must be a number!\n";
            }
            
            try {
                Integer.parseInt(bundleDiscountField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Bundle discount must be an integer!\n";
            }
            
            if (selectedProductsListView.getItems().isEmpty()) {
                errorMessage += "Bundle must contain at least one product!\n";
            }
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
        
        // Clear type-specific fields
        if (urlField != null) urlField.clear();
        if (bundleNameField != null) bundleNameField.clear();
        if (bundlePriceField != null) bundlePriceField.clear();
        if (bundleDiscountField != null) bundleDiscountField.clear();
        if (bundleDescriptionField != null) bundleDescriptionField.clear();
        if (expiryDatePicker != null) expiryDatePicker.setValue(null);
        
        // Reset product type
        typeComboBox.setValue("Regular");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}