package com.midpbo.fadjar.Entity;

import com.midpbo.fadjar.SuperEntity.product;
import java.time.LocalDate;

public class perishable_product extends product{
    private LocalDate expired_date;
    private String storage_temperature;

    public perishable_product(String name, int price, int stock, LocalDate expired_date, String storage_temperature) {
        super(name, price, stock);
        this.expired_date = expired_date;
        this.storage_temperature = storage_temperature;
    }

    public LocalDate getExpired_date() {
        return expired_date;
    }

    public void setExpired_date(LocalDate expired_date) {
        this.expired_date = expired_date;
    }

    public String getStorage_temperature() {
        return storage_temperature;
    }

    public void setStorage_temperature(String storage_temperature) {
        this.storage_temperature = storage_temperature;
    }
    
}
