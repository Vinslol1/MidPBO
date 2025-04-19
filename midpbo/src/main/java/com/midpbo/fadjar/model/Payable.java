package com.midpbo.fadjar.model;

public interface Payable {
    double calculateTotal();
    void processTransaction();
    void serializeTransaction();
}