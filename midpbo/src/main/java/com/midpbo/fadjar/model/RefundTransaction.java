package com.midpbo.fadjar.model;

import java.time.LocalDateTime;
import java.util.List;

public class RefundTransaction extends transaction implements Payable {
    private List<CartItem> items;
    
    public RefundTransaction(String transactionId, LocalDateTime date, List<CartItem> items) {
        super(transactionId, date);
        this.items = items;
    }
    
    @Override
    public double calculateTotal() {
        return items.stream().mapToDouble(CartItem::getSubtotal).sum() * -1; // Negative for refund
    }
    
    @Override
    public void processTransaction() {
        System.out.println("Refund dengan ID " + transactionId + " telah diproses");
    }
    
    @Override
    public void serializeTransaction() {
        // In a real app, this would save to database
        String transactionData = "REFUND|" + transactionId + "|" + date.toString() + "|" + calculateTotal();
        System.out.println("Saved refund: " + transactionData);
    }
}