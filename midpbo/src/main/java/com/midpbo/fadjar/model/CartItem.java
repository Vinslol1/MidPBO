package com.midpbo.fadjar.model;

public class CartItem {
    private String code;
    private String name;
    private double price;
    private int quantity;

    public CartItem(String code, String name, double price, int quantity) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // Subtotal is computed dynamically
    public double getSubtotal() {
        return price * quantity;
    }

    // For TableView bindings
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    // For existing controller compatibility
    public String getProductCode() {
        return code;
    }

    public String getProductName() {
        return name;
    }

    // Shared properties
    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
