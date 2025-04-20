package com.midpbo.fadjar.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.midpbo.fadjar.model.BundleProduct;
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
    public void addPerishableProduct(PerishableProduct product) throws SQLException {
        addProduct(product);
        String sql = "INSERT INTO perishable_products (id, expired_date) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, product.getId());
            pstmt.setDate(2, Date.valueOf(product.getExpiredDate()));
            pstmt.executeUpdate();
        }
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
}