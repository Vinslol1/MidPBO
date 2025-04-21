package com.midpbo.fadjar.Entity;

import com.midpbo.fadjar.model.Product;

public class digital_product extends Product {
    private String licenseKey;
    private String format;

    public digital_product(String code, String name, double price, String format, String licenseKey) {
        super(code, name, price, Integer.MAX_VALUE); // Digital products have unlimited stock
        this.format = format;
        this.licenseKey = licenseKey;
    }

    // Getters and setters
    public String getLicenseKey() { return licenseKey; }
    public void setLicenseKey(String licenseKey) { this.licenseKey = licenseKey; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
}