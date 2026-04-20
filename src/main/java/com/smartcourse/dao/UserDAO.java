package com.smartcourse.dao;

import com.smartcourse.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final Connection conn = DatabaseManager.getInstance().getConnection();

    public User findByEmail(String email) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE email = ? AND is_active = 1");
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            return mapUser(rs);
        return null;
    }

    public boolean existsByEmail(String email) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE email = ?");
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }

    public User findById(int userId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE user_id = ?");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            return mapUser(rs);
        return null;
    }

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM users ORDER BY role, name");
        while (rs.next())
            users.add(mapUser(rs));
        return users;
    }

    public List<User> findAllStudents() throws SQLException {
        List<User> students = new ArrayList<>();
        PreparedStatement ps = conn
                .prepareStatement("SELECT * FROM users WHERE role = 'STUDENT' AND is_active = 1 ORDER BY name");
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            students.add(mapUser(rs));
        return students;
    }

    public List<User> findAllFaculty() throws SQLException {
        List<User> faculty = new ArrayList<>();
        PreparedStatement ps = conn
                .prepareStatement("SELECT * FROM users WHERE role = 'FACULTY' AND is_active = 1 ORDER BY name");
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            faculty.add(mapUser(rs));
        return faculty;
    }

    public void insert(User user) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users (name, email, password, role, program, semester, department, student_id, faculty_id, admin_id) VALUES (?,?,?,?,?,?,?,?,?,?)");
        ps.setString(1, user.getName());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPassword());
        ps.setString(4, user.getRole());
        if (user instanceof Student s) {
            ps.setString(5, s.getProgram());
            ps.setInt(6, s.getSemester());
            ps.setNull(7, Types.VARCHAR);
            ps.setInt(8, s.getStudentId());
            ps.setNull(9, Types.INTEGER);
            ps.setNull(10, Types.INTEGER);
        } else if (user instanceof Faculty f) {
            ps.setNull(5, Types.VARCHAR);
            ps.setNull(6, Types.INTEGER);
            ps.setString(7, f.getDepartment());
            ps.setNull(8, Types.INTEGER);
            ps.setInt(9, f.getFacultyId());
            ps.setNull(10, Types.INTEGER);
        } else {
            ps.setNull(5, Types.VARCHAR);
            ps.setNull(6, Types.INTEGER);
            ps.setNull(7, Types.VARCHAR);
            ps.setNull(8, Types.INTEGER);
            ps.setNull(9, Types.INTEGER);
            ps.setNull(10, Types.INTEGER);
        }
        ps.executeUpdate();
    }

    public void update(User user) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE users SET name=?, email=?, password=?, is_active=? WHERE user_id=?");
        ps.setString(1, user.getName());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPassword());
        ps.setInt(4, 1);
        ps.setInt(5, user.getUserId());
        ps.executeUpdate();
    }

    public void deactivate(int userId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE users SET is_active = 0 WHERE user_id = ?");
        ps.setInt(1, userId);
        ps.executeUpdate();
    }

    private User mapUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        int userId = rs.getInt("user_id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String password = rs.getString("password");

        return switch (role) {
            case "STUDENT" -> new Student(userId, name, email, password,
                    rs.getInt("student_id"), rs.getString("program"), rs.getInt("semester"));
            case "FACULTY" -> new Faculty(userId, name, email, password,
                    rs.getInt("faculty_id"), rs.getString("department"));
            case "ADMIN" -> new Admin(userId, name, email, password, rs.getInt("admin_id"));
            default -> throw new SQLException("Unknown role: " + role);
        };
    }
}
