package com.smartcourse.model;

public class Schedule {
    private int scheduleId;
    private int courseId;
    private int facultyId;
    private String timeSlot; // e.g. "MON 09:00-11:00"
    private String room;
    private String courseName;
    private String facultyName;
    private String status; // PENDING, APPROVED

    public Schedule() {
    }

    public Schedule(int scheduleId, int courseId, int facultyId, String timeSlot, String room, String status) {
        this.scheduleId = scheduleId;
        this.courseId = courseId;
        this.facultyId = facultyId;
        this.timeSlot = timeSlot;
        this.room = room;
        this.status = status;
    }

    // Getters & Setters
    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
