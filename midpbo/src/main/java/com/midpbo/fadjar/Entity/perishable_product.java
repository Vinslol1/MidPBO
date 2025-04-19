package com.midpbo.fadjar.Entity;

import java.time.LocalDate;
import com.midpbo.fadjar.model.product;

public class perishable_product extends product {
    private LocalDate expiredDate;
    private String storageTemperature;

    public perishable_product(String code, String name, double price, int stock, 
                            LocalDate expiredDate, String storageTemperature) {
        super(code, name, price, stock);
        this.expiredDate = expiredDate;
        this.storageTemperature = storageTemperature;
    }

    // Getters and setters
    public LocalDate getExpiredDate() { return expiredDate; }
    public void setExpiredDate(LocalDate expiredDate) { this.expiredDate = expiredDate; }
    public String getStorageTemperature() { return storageTemperature; }
    public void setStorageTemperature(String storageTemperature) { 
        this.storageTemperature = storageTemperature; 
    }
}