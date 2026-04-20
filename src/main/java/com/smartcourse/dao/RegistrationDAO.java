package com.smartcourse.dao;

import com.smartcourse.model.Registration;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDAO {
    private final Connection conn = DatabaseManager.getInstance().getConnection();

    public List<Registration> findByStudent(int studentId) throws SQLException {
        List<Registration> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT r.*, c.name as course_name FROM registrations r " +
                        "JOIN courses c ON r.course_id = c.course_id " +
                        "WHERE r.student_id = ? ORDER BY r.registration_date DESC");
        ps.setInt(1, studentId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Registration reg = mapReg(rs);
            reg.setCourseName(rs.getString("course_name"));
            list.add(reg);
        }
        return list;
    }

    public List<Registration> findByCourse(int courseId) throws SQLException {
        List<Registration> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT r.*, u.name as student_name FROM registrations r " +
                        "JOIN users u ON r.student_id = u.user_id " +
                        "WHERE r.course_id = ? AND r.status = 'REGISTERED' ORDER BY u.name");
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Registration reg = mapReg(rs);
            reg.setStudentName(rs.getString("student_name"));
            list.add(reg);
        }
        return list;
    }

    public List<Registration> findAll() throws SQLException {
        List<Registration> list = new ArrayList<>();
        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT r.*, c.name as course_name, u.name as student_name " +
                        "FROM registrations r JOIN courses c ON r.course_id = c.course_id " +
                        "JOIN users u ON r.student_id = u.user_id ORDER BY r.registration_date DESC");
        while (rs.next()) {
            Registration reg = mapReg(rs);
            reg.setCourseName(rs.getString("course_name"));
            reg.setStudentName(rs.getString("student_name"));
            list.add(reg);
        }
        return list;
    }

    public boolean existsActiveRegistration(int studentId, int courseId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM registrations WHERE student_id=? AND course_id=? AND status IN ('REGISTERED','PAYMENT_PENDING')");
        ps.setInt(1, studentId);
        ps.setInt(2, courseId);
        ResultSet rs = ps.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    public boolean hasCompletedCourse(int studentId, int courseId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM registrations WHERE student_id=? AND course_id=? AND status='REGISTERED'");
        ps.setInt(1, studentId);
        ps.setInt(2, courseId);
        ResultSet rs = ps.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    public int insert(Registration reg) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO registrations (student_id, course_id, status, registration_date) VALUES (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, reg.getStudentId());
        ps.setInt(2, reg.getCourseId());
        ps.setString(3, reg.getStatus());
        ps.setString(4, LocalDateTime.now().toString());
        ps.executeUpdate();
        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next()) {
            int id = keys.getInt(1);
            reg.setRegId(id);
            return id;
        }
        return -1;
    }

    public void updateStatus(int regId, String status) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE registrations SET status=? WHERE reg_id=?");
        ps.setString(1, status);
        ps.setInt(2, regId);
        ps.executeUpdate();
    }

    private Registration mapReg(ResultSet rs) throws SQLException {
        return new Registration(rs.getInt("reg_id"), rs.getInt("student_id"),
                rs.getInt("course_id"), rs.getString("status"),
                LocalDateTime.parse(rs.getString("registration_date")));
    }
}
