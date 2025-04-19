package com.midpbo.fadjar.Entity;

import java.util.List;
import com.midpbo.fadjar.model.product;

public class bundle_product extends product {
    private List<product> includedProducts;
    private int bundleDiscount;

    public bundle_product(String code, String name, double basePrice, int stock,
                        List<product> includedProducts, int bundleDiscount) {
        super(code, name, basePrice, stock);
        this.includedProducts = includedProducts;
        this.bundleDiscount = bundleDiscount;
    }

    // Calculate total price of included products minus discount
    @Override
    public double getPrice() {
        double total = includedProducts.stream().mapToDouble(product::getPrice).sum();
        return total * (100 - bundleDiscount) / 100;
    }

    // Getters and setters
    public List<product> getIncludedProducts() { return includedProducts; }
    public void setIncludedProducts(List<product> includedProducts) { 
        this.includedProducts = includedProducts; 
    }
    public int getBundleDiscount() { return bundleDiscount; }
    public void setBundleDiscount(int bundleDiscount) { 
        this.bundleDiscount = bundleDiscount; 
    }
}