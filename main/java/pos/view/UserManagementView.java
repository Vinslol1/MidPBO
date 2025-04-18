package main.java.pos.view;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import main.java.pos.POSApp;
import main.java.pos.model.User;
import javafx.geometry.Insets;

public class UserManagementView {
    private POSApp app;

    public UserManagementView(POSApp app) {
        this.app = app;
    }

    public Tab createUserManagementTab() {
        Tab userTab = new Tab("User Management");
        userTab.setClosable(false);

        // Table for users
        TableView<User> userTable = new TableView<>();
        ObservableList<User> users = app.getUsers();

        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> fullNameCol = new TableColumn<>("Full Name");
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        userTable.getColumns().addAll(usernameCol, fullNameCol, emailCol);
        userTable.setItems(users);

        // Form for adding/editing users
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        Button addButton = new Button("Add User");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addButton.setOnAction(e -> {
            User newUser = new User(
                    usernameField.getText(),
                    passwordField.getText(),
                    fullNameField.getText(),
                    emailField.getText()
            );
            users.add(newUser);
            app.addLog(app.getCurrentUser().getUsername(), "Added user: " + newUser.getUsername());
            clearFields(usernameField, passwordField, fullNameField, emailField);
        });

        Button updateButton = new Button("Update Selected");
        updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        updateButton.setOnAction(e -> {
            User selectedUser = userTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                selectedUser.setFullName(fullNameField.getText());
                selectedUser.setEmail(emailField.getText());
                if (!passwordField.getText().isEmpty()) {
                    selectedUser.setPassword(passwordField.getText());
                }
                userTable.refresh();
                app.addLog(app.getCurrentUser().getUsername(), "Updated user: " + selectedUser.getUsername());
                clearFields(usernameField, passwordField, fullNameField, emailField);
            }
        });

        Button deleteButton = new Button("Delete Selected");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> {
            User selectedUser = userTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                app.addLog(app.getCurrentUser().getUsername(), "Deleted user: " + selectedUser.getUsername());
                users.remove(selectedUser);
            }
        });

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                usernameField.setText(newSelection.getUsername());
                passwordField.clear();
                fullNameField.setText(newSelection.getFullName());
                emailField.setText(newSelection.getEmail());
            }
        });

        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton);

        VBox formBox = new VBox(10);
        formBox.getChildren().addAll(
                new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                new Label("Full Name:"), fullNameField,
                new Label("Email:"), emailField,
                buttonBox
        );
        formBox.setPadding(new Insets(15));
        formBox.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd;");

        HBox contentBox = new HBox(10);
        contentBox.getChildren().addAll(userTable, formBox);
        contentBox.setPadding(new Insets(15));

        userTab.setContent(contentBox);
        return userTab;
    }

    private void clearFields(Control... fields) {
        for (Control field : fields) {
            if (field instanceof TextInputControl) {
                ((TextInputControl) field).clear();
            }
        }
    }
}