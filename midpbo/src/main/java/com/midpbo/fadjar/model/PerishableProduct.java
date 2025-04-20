package com.midpbo.fadjar.model;
import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
public class PerishableProduct extends Product {
    private ObjectProperty<LocalDate> expiredDate = new SimpleObjectProperty<>();

    public PerishableProduct(int id, String code, String name, double price, int stock, LocalDate expiredDate) {
        super(id, code, name, price, stock);
        this.expiredDate.set(expiredDate);
    }

    public LocalDate getExpiredDate() { return expiredDate.get(); }
    public ObjectProperty<LocalDate> expiredDateProperty() { return expiredDate; }
    public void setExpiredDate(LocalDate expiredDate) { this.expiredDate.set(expiredDate); }
}