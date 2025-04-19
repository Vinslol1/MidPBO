// package com.midpbo.fadjar.Entity;

// import com.midpbo.fadjar.model.product;

public class digital_product extends product {
    private String licenseKey; // Additional field specific to digital products

    // Constructor matching parent class
    public digital_product(String name, double price, int stock) {
        super(name, price, stock); // Properly calls parent constructor
        this.licenseKey = ""; // Initialize licenseKey
    }

    // Additional constructor with licenseKey
    public digital_product(String name, double price, int stock, String licenseKey) {
        super(name, price, stock);
        this.licenseKey = licenseKey;
    }

    // Getter and setter for licenseKey
    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }
}