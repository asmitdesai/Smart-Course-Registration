package com.smartcourse.dao;

import com.smartcourse.model.CourseMaterial;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseMaterialDAO {
    private final Connection conn = DatabaseManager.getInstance().getConnection();

    public List<CourseMaterial> findByCourse(int courseId) throws SQLException {
        List<CourseMaterial> materials = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT cm.*, c.name AS course_name, u.name AS faculty_name " +
                "FROM course_materials cm " +
                "JOIN courses c ON cm.course_id = c.course_id " +
                "JOIN users u ON cm.faculty_id = u.user_id " +
                "WHERE cm.course_id = ? ORDER BY cm.upload_date DESC");
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            materials.add(mapMaterial(rs));
        }
        return materials;
    }

    public List<CourseMaterial> findByFaculty(int facultyId) throws SQLException {
        List<CourseMaterial> materials = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT cm.*, c.name AS course_name, u.name AS faculty_name " +
                "FROM course_materials cm " +
                "JOIN courses c ON cm.course_id = c.course_id " +
                "JOIN users u ON cm.faculty_id = u.user_id " +
                "WHERE cm.faculty_id = ? ORDER BY cm.upload_date DESC");
        ps.setInt(1, facultyId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            materials.add(mapMaterial(rs));
        }
        return materials;
    }

    public List<CourseMaterial> findByStudentCourses(int studentId) throws SQLException {
        List<CourseMaterial> materials = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT cm.*, c.name AS course_name, u.name AS faculty_name " +
                "FROM course_materials cm " +
                "JOIN courses c ON cm.course_id = c.course_id " +
                "JOIN users u ON cm.faculty_id = u.user_id " +
                "WHERE cm.course_id IN (" +
                "  SELECT course_id FROM registrations WHERE student_id = ? AND status = 'REGISTERED'" +
                ") ORDER BY cm.upload_date DESC");
        ps.setInt(1, studentId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            materials.add(mapMaterial(rs));
        }
        return materials;
    }

    public void insert(CourseMaterial material) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO course_materials (course_id, faculty_id, title, description, material_type, content, upload_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, material.getCourseId());
        ps.setInt(2, material.getFacultyId());
        ps.setString(3, material.getTitle());
        ps.setString(4, material.getDescription());
        ps.setString(5, material.getMaterialType());
        ps.setString(6, material.getContent());
        ps.setString(7, material.getUploadDate());
        ps.executeUpdate();
        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next()) {
            material.setMaterialId(keys.getInt(1));
        }
    }

    public void update(CourseMaterial material) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE course_materials SET title=?, description=?, material_type=?, content=? WHERE material_id=?");
        ps.setString(1, material.getTitle());
        ps.setString(2, material.getDescription());
        ps.setString(3, material.getMaterialType());
        ps.setString(4, material.getContent());
        ps.setInt(5, material.getMaterialId());
        ps.executeUpdate();
    }

    public void delete(int materialId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM course_materials WHERE material_id = ?");
        ps.setInt(1, materialId);
        ps.executeUpdate();
    }

    private CourseMaterial mapMaterial(ResultSet rs) throws SQLException {
        CourseMaterial m = new CourseMaterial(
                rs.getInt("material_id"),
                rs.getInt("course_id"),
                rs.getInt("faculty_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("material_type"),
                rs.getString("content"),
                rs.getString("upload_date"));
        m.setCourseName(rs.getString("course_name"));
        m.setFacultyName(rs.getString("faculty_name"));
        return m;
    }
}
