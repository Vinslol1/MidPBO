package com.midpbo.fadjar.model;

import java.time.LocalDateTime;
import java.util.List;

public class PurchaseTransaction extends transaction implements Payable {
    private List<CartItem> items;
    
    public PurchaseTransaction(String transactionId, LocalDateTime date, List<CartItem> items) {
        super(transactionId, date);
        this.items = items;
    }
    
    @Override
    public double calculateTotal() {
        return items.stream().mapToDouble(CartItem::getSubtotal).sum() * 1.1; // 10% tax
    }
    
    @Override
    public void processTransaction() {
        System.out.println("Transaksi dengan ID " + transactionId + " telah diproses");
    }
    
    @Override
    public void serializeTransaction() {
        // In a real app, this would save to database
        String transactionData = "PURCHASE|" + transactionId + "|" + date.toString() + "|" + calculateTotal();
        System.out.println("Saved transaction: " + transactionData);
    }
}