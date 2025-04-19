package com.midpbo.fadjar.model;

public class CartItem {
    private String productCode;
    private String productName;
    private double price;
    private int quantity;
    
    public CartItem(String productCode, String productName, double price, int quantity) {
        this.productCode = productCode;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }
    
    public double getSubtotal() {
        return price * quantity;
    }
    
    // Getters and setters
    public String getProductCode() { return productCode; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}