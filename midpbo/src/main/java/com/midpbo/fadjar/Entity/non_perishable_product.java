package com.midpbo.fadjar.Entity;

import com.midpbo.fadjar.model.product;

public class non_perishable_product extends product{
    private String expiration_date;
    private String storage_condition;

    public non_perishable_product(String name, double price, int stock, String expiration_date, String storage_condition) {
        super(name, price, stock);
        this.expiration_date = expiration_date;
        this.storage_condition = storage_condition;
    }

    public String getExpirationDate() {
        return expiration_date;
    }

    public void setExpirationDate(String expiration_date) {
        this.expiration_date = expiration_date;
    }

    public String getStorageCondition() {
        return storage_condition;
    }

    public void setStorageCondition(String storage_condition) {
        this.storage_condition = storage_condition;
    }
    
}
