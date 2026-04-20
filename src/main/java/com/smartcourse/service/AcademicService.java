package com.smartcourse.service;

import com.smartcourse.dao.GradeDAO;
import com.smartcourse.dao.AttendanceDAO;
import com.smartcourse.model.Grade;
import com.smartcourse.model.Attendance;
import java.sql.SQLException;
import java.util.List;

public class AcademicService {
    private static AcademicService instance;
    private final GradeDAO gradeDAO = new GradeDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    private AcademicService() {
    }

    public static AcademicService getInstance() {
        if (instance == null)
            instance = new AcademicService();
        return instance;
    }

    public List<Grade> getStudentGrades(int studentId) throws SQLException {
        return gradeDAO.findByStudent(studentId);
    }

    public List<Grade> getCourseGrades(int courseId) throws SQLException {
        return gradeDAO.findByCourse(courseId);
    }

    public void saveGrade(Grade grade) throws SQLException {
        gradeDAO.upsert(grade);
    }

    public List<Attendance> getStudentAttendance(int studentId) throws SQLException {
        return attendanceDAO.findByStudent(studentId);
    }

    public List<Attendance> getCourseAttendance(int courseId) throws SQLException {
        return attendanceDAO.findByCourse(courseId);
    }

    public void saveAttendance(Attendance attendance) throws SQLException {
        attendanceDAO.upsert(attendance);
    }
}
