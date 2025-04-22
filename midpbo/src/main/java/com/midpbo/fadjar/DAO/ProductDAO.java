package com.midpbo.fadjar.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.midpbo.fadjar.model.BundleProduct;
import com.midpbo.fadjar.model.DigitalProduct;
import com.midpbo.fadjar.model.PerishableProduct;
import com.midpbo.fadjar.model.Product;

public class ProductDAO {
    private final Connection connection;
    public ProductDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("Fetching products..."); // Debug
            while (rs.next()) {
                Product p = new Product(
                    rs.getInt("id"),
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock")
                );
                products.add(p);
                System.out.println("Found product: " + p.getCode()); // Debug
            }
        } catch (SQLException e) {
            System.err.println("Error fetching products: " + e.getMessage());
        }
        return products;
    }

    public void addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (code, name, price, stock) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, product.getCode());
            pstmt.setString(2, product.getName());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getStock());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateProduct(Product product) throws SQLException {
        String sql = "UPDATE products SET code = ?, name = ?, price = ?, stock = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, product.getCode());
            pstmt.setString(2, product.getName());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getStock());
            pstmt.setInt(5, product.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

// PerishableProduct CRUD operations
public List<PerishableProduct> getAllPerishableProducts() {
    List<PerishableProduct> products = new ArrayList<>();
    String sql = "SELECT * FROM perishable_products";

    try (Statement stmt = connection.createStatement();
    ResultSet rs = stmt.executeQuery(sql))  {

        while (rs.next()) {
            // Get the expiry date as a string
            String dateStr = rs.getString("expired_date");
            LocalDate expiryDate = null;

            // Parse the string to LocalDate
            if (dateStr != null && !dateStr.isEmpty()) {
                try {
                    // Parse the date using DateTimeFormatter
                    expiryDate = LocalDate.parse(dateStr);
                } catch (Exception e) {
                    System.err.println("Error parsing date '" + dateStr + "': " + e.getMessage());
                }
            }

            PerishableProduct product = new PerishableProduct(
                rs.getInt("id"),
                rs.getString("code"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getInt("stock"),
                expiryDate
            );
            products.add(product);
        }
    } catch (SQLException e) {
        // Print the SQL error details to the terminal
        System.err.println("SQL Error occurred in getAllPerishableProducts: " + e.getMessage());
        System.err.println("SQL State: " + e.getSQLState());
        System.err.println("Error Code: " + e.getErrorCode());
        e.printStackTrace();
    }

    return products;
}


// For storing in database
public void addPerishableProduct(PerishableProduct product) throws SQLException {
    String sql = "INSERT INTO perishable_products (code, name, price, stock, expired_date) VALUES (?, ?, ?, ?, ?)";
    
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, product.getCode());
        pstmt.setString(2, product.getName());
        pstmt.setDouble(3, product.getPrice());
        pstmt.setInt(4, product.getStock());
        
        // Convert LocalDate to yyyy-MM-dd format
        if (product.getExpiredDate() != null) {
            String formattedDate = product.getExpiredDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            pstmt.setString(5, formattedDate);
        } else {
            pstmt.setNull(5, java.sql.Types.VARCHAR);
        }

        pstmt.executeUpdate();
        
    }
}
    public void updatePerishableProduct(PerishableProduct product) throws SQLException {
        String sql = "UPDATE perishable_products SET code = ?, name = ?, price = ?, stock = ?, expired_date = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, product.getCode());
            pstmt.setString(2, product.getName());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getStock());
            
            // Convert LocalDate to yyyy-MM-dd format
            if (product.getExpiredDate() != null) {
                String formattedDate = product.getExpiredDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                pstmt.setString(5, formattedDate);
            } else {
                pstmt.setNull(5, java.sql.Types.VARCHAR);
            }
            
            // Set the ID to identify which product to update
            pstmt.setInt(6, product.getId());
            
            pstmt.executeUpdate();
        }
    }
    public void deletePerishableProduct(int id) throws SQLException {
        String sql = "DELETE FROM perishable_products WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
        
        // Delete from base products table
        deleteProduct(id);
    }

    public List<DigitalProduct> getAllDigitalProducts() throws SQLException {
        List<DigitalProduct> products = new ArrayList<>();
        String sql = "SELECT * FROM digital_products";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                DigitalProduct p = new DigitalProduct(
                    rs.getInt("id"),
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getString("url"),
                    rs.getString("description")
                );
                products.add(p);
            }
        }
        return products;
    }
    
    public void addDigitalProduct(DigitalProduct product) throws SQLException {
        String sql = "INSERT INTO digital_products (code, url, description, name, price) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, product.getCode());
            pstmt.setString(2, product.getUrl());
            pstmt.setString(3, product.getDescription());
            pstmt.setString(4, product.getName());
            pstmt.setDouble(5, product.getPrice());
            pstmt.executeUpdate();
        } 
    }
    public void updateDigitalProduct(DigitalProduct product) throws SQLException {
        String sql = "UPDATE digital_products SET code = ?, url = ?, description = ?, name = ?, price = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, product.getCode());
            pstmt.setString(2, product.getUrl());
            pstmt.setString(3, product.getDescription());
            pstmt.setString(4, product.getName());
            pstmt.setDouble(5, product.getPrice());
            pstmt.setInt(6, product.getId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating digital product failed, no rows affected.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating digital product: " + e.getMessage());
            throw e;
        }
    }

    public void deleteDigitalProduct(int id) throws SQLException {
        String sql = "DELETE FROM digital_products WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
        
        // Delete from base products table
        deleteProduct(id);
    }

    // BundleProduct CRUD operations
    public List<BundleProduct> getAllBundleProducts() throws SQLException {
        List<BundleProduct> bundles = new ArrayList<>();
        String sql = "SELECT * FROM bundle_products";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                BundleProduct bundle = new BundleProduct(
                    rs.getInt("id"),
                    rs.getString("bundleName"),
                    rs.getDouble("bundlePrice"),
                    rs.getInt("bundleDiscount"),
                    rs.getString("bundleDescription")
                );
                
                // Load bundle items
                String itemsSql = "SELECT p.* FROM products p " +
                                 "JOIN bundle_product_items bpi ON p.id = bpi.product_id " +
                                 "WHERE bpi.bundle_id = ?";
                try (PreparedStatement itemsStmt = connection.prepareStatement(itemsSql)) {
                    itemsStmt.setInt(1, bundle.getId());
                    try (ResultSet itemsRs = itemsStmt.executeQuery()) {
                        while (itemsRs.next()) {
                            bundle.getItems().add(new Product(
                                itemsRs.getInt("id"),
                                itemsRs.getString("code"),
                                itemsRs.getString("name"),
                                itemsRs.getDouble("price"),
                                itemsRs.getInt("stock")
                            ));
                        }
                    }
                }
                
                bundles.add(bundle);
            }
        }
        return bundles;
    }

    public void addBundleProduct(BundleProduct bundle) throws SQLException {
        String sql = "INSERT INTO bundle_products (bundleName, bundlePrice, bundleDiscount, bundleDescription) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, bundle.getBundleName());
            pstmt.setDouble(2, bundle.getBundlePrice());
            pstmt.setInt(3, bundle.getBundleDiscount());
            pstmt.setString(4, bundle.getBundleDescription());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    bundle.setId(generatedKeys.getInt(1));
                }
            }
        }
        
        // Add bundle items
        addBundleItems(bundle);
    }

    public void updateBundleProduct(BundleProduct bundle) throws SQLException {
        String sql = "UPDATE bundle_products SET bundleName = ?, bundlePrice = ?, bundleDiscount = ?, bundleDescription = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, bundle.getBundleName());
            pstmt.setDouble(2, bundle.getBundlePrice());
            pstmt.setInt(3, bundle.getBundleDiscount());
            pstmt.setString(4, bundle.getBundleDescription());
            pstmt.setInt(5, bundle.getId());
            pstmt.executeUpdate();
        }
        
        // Delete existing bundle items
        String deleteItemsSql = "DELETE FROM bundle_product_items WHERE bundle_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteItemsSql)) {
            pstmt.setInt(1, bundle.getId());
            pstmt.executeUpdate();
        }
        
        // Add updated bundle items
        addBundleItems(bundle);
    }

    public void deleteBundleProduct(int id) throws SQLException {
        // Delete bundle items first
        String deleteItemsSql = "DELETE FROM bundle_product_items WHERE bundle_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteItemsSql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
        
        // Delete bundle
        String deleteBundleSql = "DELETE FROM bundle_products WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteBundleSql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private void addBundleItems(BundleProduct bundle) throws SQLException {
        String sql = "INSERT INTO bundle_product_items (bundle_id, product_id) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (Product product : bundle.getItems()) {
                pstmt.setInt(1, bundle.getId());
                pstmt.setInt(2, product.getId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
    
    // Method to get all products of all types
    public List<Object> getAllProductsOfAllTypes() throws SQLException {
        List<Object> allProducts = new ArrayList<>();
        
        // Add regular products
        allProducts.addAll(getAllProducts());
        
        // Add perishable products
        allProducts.addAll(getAllPerishableProducts());
        
        // Add digital products
        allProducts.addAll(getAllDigitalProducts());
        
        // Add bundle products
        allProducts.addAll(getAllBundleProducts());
        
        return allProducts;
    }
}
