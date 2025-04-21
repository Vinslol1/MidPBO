package com.midpbo.fadjar.model;

public class TransactionRecord {
    private String transactionId;
    private String date;
    private String type;
    private double total;

    public TransactionRecord(String transactionId, String date, String type, double total) {
        this.transactionId = transactionId;
        this.date = date;
        this.type = type;
        this.total = total;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public double getTotal() {
        return total;
    }
}
