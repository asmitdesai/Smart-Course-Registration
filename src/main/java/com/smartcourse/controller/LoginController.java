package com.smartcourse.controller;

import com.smartcourse.model.User;
import com.smartcourse.service.AuthService;
import com.smartcourse.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button loginBtn;

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter your email and password.");
            return;
        }

        loginBtn.setDisable(true);
        loginBtn.setText("Signing in...");

        try {
            User user = AuthService.getInstance().login(email, password);
            if (user == null) {
                showError("❌ Invalid credentials. Please try again.");
                loginBtn.setDisable(false);
                loginBtn.setText("Sign In →");
                return;
            }

            // Route based on role
            String role = user.getRole();
            if ("STUDENT".equals(role)) {
                SceneManager.switchScene("/fxml/student_dashboard.fxml", "Student Portal — " + user.getName());
            } else if ("FACULTY".equals(role)) {
                SceneManager.switchScene("/fxml/faculty_dashboard.fxml", "Faculty Portal — " + user.getName());
            } else if ("ADMIN".equals(role)) {
                SceneManager.switchScene("/fxml/admin_dashboard.fxml", "Admin Portal — " + user.getName());
            }
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
            loginBtn.setDisable(false);
            loginBtn.setText("Sign In →");
        }
    }

    @FXML
    private void handleNavigateToSignup() {
        SceneManager.switchScene("/fxml/signup.fxml", "Smart Course Registration - Create Account");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
