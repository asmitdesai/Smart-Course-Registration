package com.smartcourse.dao;

import com.smartcourse.model.Grade;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {
    private final Connection conn = DatabaseManager.getInstance().getConnection();

    public List<Grade> findByStudent(int studentId) throws SQLException {
        List<Grade> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT g.*, c.name as course_name FROM grades g " +
                        "JOIN courses c ON g.course_id = c.course_id WHERE g.student_id = ?");
        ps.setInt(1, studentId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Grade g = mapGrade(rs);
            g.setCourseName(rs.getString("course_name"));
            list.add(g);
        }
        return list;
    }

    public List<Grade> findByCourse(int courseId) throws SQLException {
        List<Grade> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT g.*, u.name as student_name FROM grades g " +
                        "JOIN users u ON g.student_id = u.user_id WHERE g.course_id = ? ORDER BY u.name");
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Grade g = mapGrade(rs);
            g.setStudentName(rs.getString("student_name"));
            list.add(g);
        }
        return list;
    }

    public void upsert(Grade grade) throws SQLException {
        PreparedStatement check = conn.prepareStatement(
                "SELECT grade_id FROM grades WHERE student_id=? AND course_id=?");
        check.setInt(1, grade.getStudentId());
        check.setInt(2, grade.getCourseId());
        ResultSet rs = check.executeQuery();
        if (rs.next()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE grades SET marks=?, letter_grade=? WHERE student_id=? AND course_id=?");
            ps.setDouble(1, grade.getMarks());
            ps.setString(2, grade.getLetterGrade());
            ps.setInt(3, grade.getStudentId());
            ps.setInt(4, grade.getCourseId());
            ps.executeUpdate();
        } else {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO grades (student_id, course_id, marks, letter_grade) VALUES (?,?,?,?)");
            ps.setInt(1, grade.getStudentId());
            ps.setInt(2, grade.getCourseId());
            ps.setDouble(3, grade.getMarks());
            ps.setString(4, grade.getLetterGrade());
            ps.executeUpdate();
        }
    }

    private Grade mapGrade(ResultSet rs) throws SQLException {
        Grade g = new Grade();
        g.setGradeId(rs.getInt("grade_id"));
        g.setStudentId(rs.getInt("student_id"));
        g.setCourseId(rs.getInt("course_id"));
        g.setMarks(rs.getDouble("marks"));
        g.setLetterGrade(rs.getString("letter_grade"));
        return g;
    }
}
