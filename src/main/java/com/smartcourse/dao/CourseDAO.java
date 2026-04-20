package com.smartcourse.dao;

import com.smartcourse.model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    private final Connection conn = DatabaseManager.getInstance().getConnection();

    public List<Course> findAll() throws SQLException {
        List<Course> courses = new ArrayList<>();
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM courses ORDER BY name");
        while (rs.next()) {
            Course c = mapCourse(rs);
            c.setPrerequisiteIds(findPrerequisiteIds(c.getCourseId()));
            courses.add(c);
        }
        return courses;
    }

    public Course findById(int courseId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM courses WHERE course_id = ?");
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Course c = mapCourse(rs);
            c.setPrerequisiteIds(findPrerequisiteIds(courseId));
            return c;
        }
        return null;
    }

    public List<Integer> findPrerequisiteIds(int courseId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT prerequisite_id FROM course_prerequisites WHERE course_id = ?");
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            ids.add(rs.getInt("prerequisite_id"));
        return ids;
    }

    public void insert(Course course) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO courses (name, credits, seats_available, total_seats, description) VALUES (?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, course.getName());
        ps.setInt(2, course.getCredits());
        ps.setInt(3, course.getSeatsAvailable());
        ps.setInt(4, course.getTotalSeats());
        ps.setString(5, course.getDescription());
        ps.executeUpdate();
        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next())
            course.setCourseId(keys.getInt(1));
    }

    public void update(Course course) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE courses SET name=?, credits=?, seats_available=?, total_seats=?, description=? WHERE course_id=?");
        ps.setString(1, course.getName());
        ps.setInt(2, course.getCredits());
        ps.setInt(3, course.getSeatsAvailable());
        ps.setInt(4, course.getTotalSeats());
        ps.setString(5, course.getDescription());
        ps.setInt(6, course.getCourseId());
        ps.executeUpdate();
    }

    public void decrementSeats(int courseId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE courses SET seats_available = seats_available - 1 WHERE course_id = ? AND seats_available > 0");
        ps.setInt(1, courseId);
        ps.executeUpdate();
    }

    public void incrementSeats(int courseId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE courses SET seats_available = seats_available + 1 WHERE course_id = ?");
        ps.setInt(1, courseId);
        ps.executeUpdate();
    }

    public void delete(int courseId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM courses WHERE course_id = ?");
        ps.setInt(1, courseId);
        ps.executeUpdate();
    }

    public void addPrerequisite(int courseId, int prerequisiteId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT OR IGNORE INTO course_prerequisites (course_id, prerequisite_id) VALUES (?,?)");
        ps.setInt(1, courseId);
        ps.setInt(2, prerequisiteId);
        ps.executeUpdate();
    }

    public void removePrerequisites(int courseId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM course_prerequisites WHERE course_id = ?");
        ps.setInt(1, courseId);
        ps.executeUpdate();
    }

    private Course mapCourse(ResultSet rs) throws SQLException {
        return new Course(
                rs.getInt("course_id"), rs.getString("name"), rs.getInt("credits"),
                rs.getInt("seats_available"), rs.getInt("total_seats"), rs.getString("description"));
    }
}
