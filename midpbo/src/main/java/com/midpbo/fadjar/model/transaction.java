package com.midpbo.fadjar.model;

import java.time.LocalDateTime;

public abstract class transaction {
    protected String transactionId;
    protected LocalDateTime date;

    public transaction(String transactionId, LocalDateTime date) {
        this.transactionId = transactionId;
        this.date = date;
    }

    // Abstract methods for subclass implementation
    public abstract void processTransaction();
    public abstract void serializeTransaction();

    // Getters
    public String getTransactionId() { return transactionId; }
    public LocalDateTime getDate() { return date; }
}
