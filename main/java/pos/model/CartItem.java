package main.java.pos.model;

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

    // Getters
    public String getProductCode() { return productCode; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public double getSubtotal() { return price * quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
}