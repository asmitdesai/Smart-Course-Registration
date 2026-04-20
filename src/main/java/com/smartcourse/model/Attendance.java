package com.smartcourse.model;

public class Attendance {
    private int attendanceId;
    private int studentId;
    private int courseId;
    private int totalClasses;
    private int attendedClasses;
    private double percentage;
    private String studentName;
    private String courseName;

    public Attendance() {
    }

    public Attendance(int attendanceId, int studentId, int courseId, int totalClasses, int attendedClasses) {
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.totalClasses = totalClasses;
        this.attendedClasses = attendedClasses;
        this.percentage = calculatePercentage();
    }

    public double calculatePercentage() {
        if (totalClasses == 0)
            return 0.0;
        return ((double) attendedClasses / totalClasses) * 100.0;
    }

    // Getters & Setters
    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
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

    public int getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(int totalClasses) {
        this.totalClasses = totalClasses;
        this.percentage = calculatePercentage();
    }

    public int getAttendedClasses() {
        return attendedClasses;
    }

    public void setAttendedClasses(int attendedClasses) {
        this.attendedClasses = attendedClasses;
        this.percentage = calculatePercentage();
    }

    public double getPercentage() {
        return percentage;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
