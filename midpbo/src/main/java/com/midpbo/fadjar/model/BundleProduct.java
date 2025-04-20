package com.midpbo.fadjar.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BundleProduct {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty bundleName = new SimpleStringProperty();
    private DoubleProperty bundlePrice = new SimpleDoubleProperty();
    private IntegerProperty bundleDiscount = new SimpleIntegerProperty();
    private StringProperty bundleDescription = new SimpleStringProperty();
    private ObservableList<Product> items = FXCollections.observableArrayList();

    public BundleProduct(int id, String bundleName, double bundlePrice, int bundleDiscount, String bundleDescription) {
        this.id.set(id);
        this.bundleName.set(bundleName);
        this.bundlePrice.set(bundlePrice);
        this.bundleDiscount.set(bundleDiscount);
        this.bundleDescription.set(bundleDescription);
    }

    // Getters and setters for properties
    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getBundleName() { return bundleName.get(); }
    public StringProperty bundleNameProperty() { return bundleName; }
    public void setBundleName(String bundleName) { this.bundleName.set(bundleName); }

    public double getBundlePrice() { return bundlePrice.get(); }
    public DoubleProperty bundlePriceProperty() { return bundlePrice; }
    public void setBundlePrice(double bundlePrice) { this.bundlePrice.set(bundlePrice); }

    public int getBundleDiscount() { return bundleDiscount.get(); }
    public IntegerProperty bundleDiscountProperty() { return bundleDiscount; }
    public void setBundleDiscount(int bundleDiscount) { this.bundleDiscount.set(bundleDiscount); }

    public String getBundleDescription() { return bundleDescription.get(); }
    public StringProperty bundleDescriptionProperty() { return bundleDescription; }
    public void setBundleDescription(String bundleDescription) { this.bundleDescription.set(bundleDescription); }

    public ObservableList<Product> getItems() { return items; }
    public void setItems(ObservableList<Product> items) { this.items = items; }
}