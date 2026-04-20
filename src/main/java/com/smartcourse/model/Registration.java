package com.smartcourse.model;

import java.time.LocalDateTime;

public class Registration {
    private int regId;
    private int studentId;
    private int courseId;
    private String status; // PENDING, REGISTERED, DROPPED, PAYMENT_PENDING
    private LocalDateTime registrationDate;
    private String courseName; // for display
    private String studentName; // for display

    public Registration() {
    }

    public Registration(int regId, int studentId, int courseId, String status, LocalDateTime registrationDate) {
        this.regId = regId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.status = status;
        this.registrationDate = registrationDate;
    }

    public void addCourse() {
        this.status = "REGISTERED";
    }

    public void dropCourse() {
        this.status = "DROPPED";
    }

    // Getters & Setters
    public int getRegId() {
        return regId;
    }

    public void setRegId(int regId) {
        this.regId = regId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
