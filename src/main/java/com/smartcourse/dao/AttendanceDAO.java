package com.smartcourse.dao;

import com.smartcourse.model.Attendance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {
    private final Connection conn = DatabaseManager.getInstance().getConnection();

    public List<Attendance> findByStudent(int studentId) throws SQLException {
        List<Attendance> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT a.*, c.name as course_name FROM attendance a " +
                        "JOIN courses c ON a.course_id = c.course_id WHERE a.student_id = ?");
        ps.setInt(1, studentId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Attendance a = mapAttendance(rs);
            a.setCourseName(rs.getString("course_name"));
            list.add(a);
        }
        return list;
    }

    public List<Attendance> findByCourse(int courseId) throws SQLException {
        List<Attendance> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT a.*, u.name as student_name FROM attendance a " +
                        "JOIN users u ON a.student_id = u.user_id WHERE a.course_id = ? ORDER BY u.name");
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Attendance a = mapAttendance(rs);
            a.setStudentName(rs.getString("student_name"));
            list.add(a);
        }
        return list;
    }

    public void upsert(Attendance attendance) throws SQLException {
        PreparedStatement check = conn.prepareStatement(
                "SELECT attendance_id FROM attendance WHERE student_id=? AND course_id=?");
        check.setInt(1, attendance.getStudentId());
        check.setInt(2, attendance.getCourseId());
        ResultSet rs = check.executeQuery();
        if (rs.next()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE attendance SET total_classes=?, attended_classes=? WHERE student_id=? AND course_id=?");
            ps.setInt(1, attendance.getTotalClasses());
            ps.setInt(2, attendance.getAttendedClasses());
            ps.setInt(3, attendance.getStudentId());
            ps.setInt(4, attendance.getCourseId());
            ps.executeUpdate();
        } else {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO attendance (student_id, course_id, total_classes, attended_classes) VALUES (?,?,?,?)");
            ps.setInt(1, attendance.getStudentId());
            ps.setInt(2, attendance.getCourseId());
            ps.setInt(3, attendance.getTotalClasses());
            ps.setInt(4, attendance.getAttendedClasses());
            ps.executeUpdate();
        }
    }

    private Attendance mapAttendance(ResultSet rs) throws SQLException {
        return new Attendance(rs.getInt("attendance_id"), rs.getInt("student_id"),
                rs.getInt("course_id"), rs.getInt("total_classes"), rs.getInt("attended_classes"));
    }
}
