package com.smartcourse.model;

public class Student extends User {
    private int studentId;
    private String program;
    private int semester;

    public Student() {
        super();
        this.role = "STUDENT";
    }

    public Student(int userId, String name, String email, String password, int studentId, String program,
            int semester) {
        super(userId, name, email, password, "STUDENT");
        this.studentId = studentId;
        this.program = program;
        this.semester = semester;
    }

    public void viewCourses() {
    }

    public void registerCourse(int courseId) {
    }

    public void payFees() {
    }

    public void viewAcademicInfo() {
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }
}
