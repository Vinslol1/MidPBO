package com.midpbo.fadjar.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Product {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty code = new SimpleStringProperty();
    private StringProperty name = new SimpleStringProperty();
    private DoubleProperty price = new SimpleDoubleProperty();
    private IntegerProperty stock = new SimpleIntegerProperty();

    public Product(int id, String code, String name, double price, int stock) {
        this.id.set(id);
        this.code.set(code);
        this.name.set(name);
        this.price.set(price);
        this.stock.set(stock);
    }
    // Constructor without ID (for new creations)
    public Product(String code, String name, double price, int stock) {
        this(0, code, name, price, stock); // ID 0 for new products
    }

    
    // Getters and setters for properties
    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getCode() { return code.get(); }
    public StringProperty codeProperty() { return code; }
    public void setCode(String code) { this.code.set(code); }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }
    public void setName(String name) { this.name.set(name); }

    public double getPrice() { return price.get(); }
    public DoubleProperty priceProperty() { return price; }
    public void setPrice(double price) { this.price.set(price); }

    public int getStock() { return stock.get(); }
    public IntegerProperty stockProperty() { return stock; }
    public void setStock(int stock) { this.stock.set(stock); }
}