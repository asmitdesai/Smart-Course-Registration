package com.smartcourse.model;

public class Admin extends User {
    private int adminId;

    public Admin() {
        super();
        this.role = "ADMIN";
    }

    public Admin(int userId, String name, String email, String password, int adminId) {
        super(userId, name, email, password, "ADMIN");
        this.adminId = adminId;
    }

    public void manageUsers() {
    }

    public void createCourse() {
    }

    public void generateSchedule() {
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
}
