package com.midpbo.fadjar.controller;

import com.midpbo.fadjar.model.TransactionRecord;
import com.midpbo.fadjar.util.TransactionStore;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TransactionTabController {

    @FXML private TableView<TransactionRecord> transactionTable;
    @FXML private TableColumn<TransactionRecord, String> idColumn;
    @FXML private TableColumn<TransactionRecord, String> dateColumn;
    @FXML private TableColumn<TransactionRecord, String> typeColumn;
    @FXML private TableColumn<TransactionRecord, Double> totalColumn;    

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        transactionTable.setItems(TransactionStore.getAll());
    }
}
