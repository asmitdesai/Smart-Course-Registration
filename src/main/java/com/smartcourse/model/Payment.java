package com.smartcourse.model;

public class Payment {
    private int paymentId;
    private int studentId;
    private int courseId;
    private double amount;
    private String status; // PENDING, SUCCESS, FAILED
    private String cardNumber;

    public Payment() {
    }

    public Payment(int paymentId, int studentId, int courseId, double amount, String status) {
        this.paymentId = paymentId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.amount = amount;
        this.status = status;
    }

    public void processPayment() {
        PaymentGateway gateway = new PaymentGateway();
        boolean success = gateway.process(amount);
        this.status = success ? "SUCCESS" : "FAILED";
    }

    // Getters & Setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
