package com.midpbo.fadjar.controller;

import com.midpbo.fadjar.MainApp;
import com.midpbo.fadjar.model.User;
import com.midpbo.fadjar.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AuthController {
    // Common elements
    @FXML private Label messageLabel;
    
    // Login elements
    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;
    @FXML private Button loginButton;
    @FXML private Hyperlink gotoSignup;
    
    // Signup elements
    @FXML private TextField signupUsername;
    @FXML private PasswordField signupPassword;
    @FXML private PasswordField confirmPassword;
    @FXML private TextField fullName;
    @FXML private Button signupButton;
    @FXML private Hyperlink gotoLogin;

    @FXML
    private void initialize() {
        // Login to Signup navigation
        if (gotoSignup != null) {
            gotoSignup.setOnAction(e -> {
                try {
                    MainApp.showSignupView();
                } catch (Exception ex) {
                    showError("Failed to load signup view");
                }
            });
        }
        
        // Signup to Login navigation
        if (gotoLogin != null) {
            gotoLogin.setOnAction(e -> {
                try {
                    MainApp.showLoginView();
                } catch (Exception ex) {
                    showError("Failed to load login view");
                }
            });
        }
    }
    
    @FXML
    private void handleLogin() {
        String username = loginUsername.getText().trim();
        String password = loginPassword.getText().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill all fields");
            return;
        }
        
        User user = UserService.authenticate(username, password);
        if (user != null) {
            showSuccess("Login successful! Welcome, " + user.getFullName());
            try {
                MainApp.showPOSView();
            } catch (Exception e) {
                showError("Failed to load POS system");
            }
        } else {
            showError("Invalid credentials");
        }
    }
    
    @FXML
    private void handleSignup() {
        String username = signupUsername.getText().trim();
        String password = signupPassword.getText().trim();
        String confirm = confirmPassword.getText().trim();
        String name = fullName.getText().trim();
        
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty() || name.isEmpty()) {
            showError("Please fill all fields");
            return;
        }
        
        if (!password.equals(confirm)) {
            showError("Passwords don't match");
            return;
        }
        
        if (!isPasswordStrong(password)) {
            showError("Password must be at least 8 characters with:\n" +
                     "- Uppercase & lowercase letters\n" +
                     "- A number\n" +
                     "- A special character");
            return;
        }
        
        if (UserService.registerUser(username, name, password)) {
            showSuccess("Account created successfully!");
            try {
                MainApp.showLoginView();
            } catch (Exception e) {
                showError("Failed to load login view");
            }
        } else {
            showError("Username already exists");
        }
    }
    
    private boolean isPasswordStrong(String password) {
        // At least 8 chars, contains digit, lowercase, uppercase, and special char
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        return password.matches(pattern);
    }
    
    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: red;");
    }
    
    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: green;");
    }
}