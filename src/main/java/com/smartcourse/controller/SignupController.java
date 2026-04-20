package com.smartcourse.controller;

import com.smartcourse.model.*;
import com.smartcourse.service.AuthService;
import com.smartcourse.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.sql.SQLException;

public class SignupController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> roleComboBox;

    // Role-specific VBoxes
    @FXML
    private VBox studentFields;
    @FXML
    private VBox facultyFields;
    @FXML
    private VBox adminFields;

    // Student specific
    @FXML
    private TextField studentIdField;
    @FXML
    private TextField programField;
    @FXML
    private TextField semesterField;

    // Faculty specific
    @FXML
    private TextField facultyIdField;
    @FXML
    private TextField departmentField;

    // Admin specific
    @FXML
    private TextField adminIdField;

    @FXML
    private Label errorLabel;
    @FXML
    private Label successLabel;

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("STUDENT", "FACULTY", "ADMIN");
        roleComboBox.setOnAction(e -> handleRoleChange());

        // Hide all specific fields initially
        hideAllRoleFields();
    }

    private void handleRoleChange() {
        hideAllRoleFields();
        String selectedRole = roleComboBox.getValue();
        if ("STUDENT".equals(selectedRole)) {
            studentFields.setVisible(true);
            studentFields.setManaged(true);
        } else if ("FACULTY".equals(selectedRole)) {
            facultyFields.setVisible(true);
            facultyFields.setManaged(true);
        } else if ("ADMIN".equals(selectedRole)) {
            adminFields.setVisible(true);
            adminFields.setManaged(true);
        }
    }

    private void hideAllRoleFields() {
        studentFields.setVisible(false);
        studentFields.setManaged(false);
        facultyFields.setVisible(false);
        facultyFields.setManaged(false);
        adminFields.setVisible(false);
        adminFields.setManaged(false);
    }

    @FXML
    private void handleSignup() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            showError("Please fill in all basic fields.");
            return;
        }

        try {
            User user;
            if ("STUDENT".equals(role)) {
                int studentId = Integer.parseInt(studentIdField.getText().trim());
                String program = programField.getText().trim();
                int semester = Integer.parseInt(semesterField.getText().trim());
                user = new Student(0, name, email, password, studentId, program, semester);
            } else if ("FACULTY".equals(role)) {
                int facultyId = Integer.parseInt(facultyIdField.getText().trim());
                String department = departmentField.getText().trim();
                user = new Faculty(0, name, email, password, facultyId, department);
            } else if ("ADMIN".equals(role)) {
                int adminId = Integer.parseInt(adminIdField.getText().trim());
                user = new Admin(0, name, email, password, adminId);
            } else {
                showError("Please select a valid role.");
                return;
            }

            AuthService.getInstance().register(user);
            showSuccess("Registration successful! Redirecting to login...");

            // Navigate back to login after 2 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
                javafx.application.Platform.runLater(() -> handleBackToLogin());
            }).start();

        } catch (NumberFormatException e) {
            showError("IDs and Semester must be numbers.");
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleBackToLogin() {
        SceneManager.switchScene("/fxml/login.fxml", "Smart Course Registration - Login");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);
    }

    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        errorLabel.setVisible(false);
    }
}
