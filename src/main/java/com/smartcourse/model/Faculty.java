package com.smartcourse.model;

public class Faculty extends User {
    private int facultyId;
    private String department;

    public Faculty() {
        super();
        this.role = "FACULTY";
    }

    public Faculty(int userId, String name, String email, String password, int facultyId, String department) {
        super(userId, name, email, password, "FACULTY");
        this.facultyId = facultyId;
        this.department = department;
    }

    public void uploadMaterial(String material) {
    }

    public void markAttendance(int studentId, boolean present) {
    }

    public void evaluateStudent(int studentId, double marks) {
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
