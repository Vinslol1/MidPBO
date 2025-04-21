package com.midpbo.fadjar.util;

import com.midpbo.fadjar.model.TransactionRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TransactionStore {
    private static final ObservableList<TransactionRecord> transactions = FXCollections.observableArrayList();

    public static void add(TransactionRecord record) {
        transactions.add(record);
    }

    public static ObservableList<TransactionRecord> getAll() {
        return transactions;
    }
}
