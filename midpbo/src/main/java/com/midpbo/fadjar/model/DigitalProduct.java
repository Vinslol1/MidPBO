package com.midpbo.fadjar.model;

public class DigitalProduct extends Product {
    private String url;
    private String description;
    
    public DigitalProduct(int id, String code, String name, double price, String url, String description) {
        super(id, code, name, price, 0); // Pass 0 for stock since digital products don't need it
        this.url = url;
        this.description = description;
    }
    
    // Override getStock to always return -1 or 0 for digital products
    @Override
    public int getStock() {
        return -1; // or 0, to indicate digital products don't have stock
    }
    
    // Optionally override setStock to do nothing
    @Override
    public void setStock(int stock) {
        // Digital products don't track stock, so ignore
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return super.toString().replace(", Stock: " + super.getStock(), "") + 
               " [URL: " + url + ", Description: " + description + "]";
    }
}