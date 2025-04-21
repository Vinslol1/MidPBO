package com.midpbo.fadjar.Entity;

import java.time.LocalDate;
import com.midpbo.fadjar.model.Product;

public class perishable_product extends Product {
    private LocalDate expiredDate;

    public perishable_product(String code, String name, double price, int stock, 
                            LocalDate expiredDate) {
        super(code, name, price, stock);
        this.expiredDate = expiredDate;
    }

    // Getters and setters
    public LocalDate getExpiredDate() { return expiredDate; }
    public void setExpiredDate(LocalDate expiredDate) { this.expiredDate = expiredDate; }
}