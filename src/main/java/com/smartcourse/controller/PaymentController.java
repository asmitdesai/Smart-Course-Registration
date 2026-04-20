package com.smartcourse.controller;

import com.smartcourse.model.*;
import com.smartcourse.service.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;

public class PaymentController {
    @FXML
    private Label courseNameLabel;
    @FXML
    private Label creditsLabel;
    @FXML
    private Label totalAmountLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private TextField cardNameField;
    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField expiryField;
    @FXML
    private PasswordField cvvField;

    private Course course;
    private Registration registration;
    private Student student;
    private final RegistrationService registrationService = RegistrationService.getInstance();
    private StudentDashboardController dashboardController;

    public void setPaymentData(Student student, Course course, Registration reg, StudentDashboardController dashboard) {
        this.student = student;
        this.course = course;
        this.registration = reg;
        this.dashboardController = dashboard;

        courseNameLabel.setText(course.getName());
        creditsLabel.setText(String.valueOf(course.getCredits()));
        double amount = course.getCredits() * 150.0;
        totalAmountLabel.setText(String.format("%.2f USD", amount));
    }

    @FXML
    private void handleProcessPayment() {
        if (cardNameField.getText().isEmpty() || cardNumberField.getText().isEmpty() ||
                expiryField.getText().isEmpty() || cvvField.getText().isEmpty()) {
            showStatus("Please fill in all payment details.", "error-label");
            return;
        }

        try {
            boolean success = registrationService.processPayment(student.getUserId(), course.getCourseId(),
                    registration.getRegId());
            if (success) {
                showStatus("✅ Payment Successful! You are now registered.", "success-label");
                // Wait a bit or redirect immediately
                dashboardController.showRegistrations();
            } else {
                showStatus("❌ Registration failed. Payment declined or seats no longer available.", "error-label");
            }
        } catch (SQLException e) {
            showStatus("Error: " + e.getMessage(), "error-label");
        }
    }

    @FXML
    private void handleCancel() {
        dashboardController.showBrowse();
    }

    private void showStatus(String message, String styleClass) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().setAll(styleClass);
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }
}
