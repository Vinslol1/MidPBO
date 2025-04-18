package main.java.pos.model;

import java.time.LocalDateTime;
import java.util.List;

public class Transaction {
    private static int nextId = 1;

    private int id;
    private String timestamp;
    private String cashier;
    private List<CartItem> items;
    private double total;
    private double payment;
    private double change;

    public Transaction(LocalDateTime timestamp, String cashier, List<CartItem> items, double total, double payment, double change) {
        this.id = nextId++;
        this.timestamp = timestamp.toString();
        this.cashier = cashier;
        this.items = items;
        this.total = total;
        this.payment = payment;
        this.change = change;
    }

    // Getters
    public int getId() { return id; }
    public String getTimestamp() { return timestamp; }
    public String getCashier() { return cashier; }
    public List<CartItem> getItems() { return items; }
    public double getTotal() { return total; }
    public double getPayment() { return payment; }
    public double getChange() { return change; }
}