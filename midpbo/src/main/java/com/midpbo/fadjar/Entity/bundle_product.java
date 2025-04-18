package com.midpbo.fadjar.Entity;

import java.util.List;

import com.midpbo.fadjar.model.product;

import java.util.ArrayList;

public class bundle_product extends product {
    private String bundleName;
    private int bundlePrice;
    private int bundleDiscount;
    private String bundleDescription;
    private List<product> products;

    
    public bundle_product(List<product> products, String name, double price, int stock,String bundleName, int bundlePrice, int bundleDiscount, String bundleDescription) {
        super(name, price, stock);
        this.products = products;
        this.bundleName = bundleName;
        this.bundlePrice = bundlePrice;
        this.bundleDiscount = bundleDiscount;
        this.bundleDescription = bundleDescription;
    }
    
    // public void addBundleProduct(String name, double price, int stock){
    //     List<product> product = new ArrayList<>();
    //         product.add(new product(name, price, stock));
    // }

    public void addAllProducts(List<product> products){
        this.products.addAll(products);
    }
    public List<product> getProducts() {
        return products;
    }

    public String getBundleName() {
        return bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    public int getBundlePrice() {
        return bundlePrice;
    }

    public void setBundlePrice(int bundlePrice) {
        this.bundlePrice = bundlePrice;
    }

    public int getBundleDiscount() {
        return bundleDiscount;
    }

    public void setBundleDiscount(int bundleDiscount) {
        this.bundleDiscount = bundleDiscount;
    }

    public String getBundleDescription() {
        return bundleDescription;
    }

    public void setBundleDescription(String bundleDescription) {
        this.bundleDescription = bundleDescription;
    }
    
    //Method
    public double getDiscountedPrice(){
        return bundlePrice - (bundlePrice * bundleDiscount / 100);
    }
}
