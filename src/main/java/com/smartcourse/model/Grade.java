package com.smartcourse.model;

public class Grade {
    private int gradeId;
    private int studentId;
    private int courseId;
    private double marks;
    private String letterGrade;
    private String studentName;
    private String courseName;

    public Grade() {
    }

    public Grade(int gradeId, int studentId, int courseId, double marks) {
        this.gradeId = gradeId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.marks = marks;
        this.letterGrade = calculateGrade(marks);
    }

    public String calculateGrade(double marks) {
        if (marks >= 90)
            return "A+";
        if (marks >= 80)
            return "A";
        if (marks >= 70)
            return "B";
        if (marks >= 60)
            return "C";
        if (marks >= 50)
            return "D";
        return "F";
    }

    public void recalculate() {
        this.letterGrade = calculateGrade(marks);
    }

    // Getters & Setters
    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
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

    public double getMarks() {
        return marks;
    }

    public void setMarks(double marks) {
        this.marks = marks;
        this.letterGrade = calculateGrade(marks);
    }

    public String getLetterGrade() {
        return letterGrade;
    }

    public void setLetterGrade(String letterGrade) {
        this.letterGrade = letterGrade;
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
