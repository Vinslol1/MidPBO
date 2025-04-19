package com.midpbo.fadjar.Entity;

import com.midpbo.fadjar.model.product;

public class non_perishable_product extends product {
    private String expirationDate;
    private String storageCondition;

    public non_perishable_product(String code, String name, double price, int stock,
                                String expirationDate, String storageCondition) {
        super(code, name, price, stock);
        this.expirationDate = expirationDate;
        this.storageCondition = storageCondition;
    }

    // Getters and setters
    public String getExpirationDate() { return expirationDate; }
    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }
    public String getStorageCondition() { return storageCondition; }
    public void setStorageCondition(String storageCondition) { 
        this.storageCondition = storageCondition; 
    }
}