package com.smartcourse.dao;

import com.smartcourse.model.Schedule;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {
    private final Connection conn = DatabaseManager.getInstance().getConnection();

    public List<Schedule> findAll() throws SQLException {
        List<Schedule> list = new ArrayList<>();
        ResultSet rs = conn.createStatement().executeQuery(
                "SELECT s.*, c.name as course_name, u.name as faculty_name " +
                        "FROM schedules s JOIN courses c ON s.course_id = c.course_id " +
                        "JOIN users u ON s.faculty_id = u.user_id ORDER BY s.time_slot");
        while (rs.next())
            list.add(mapSchedule(rs));
        return list;
    }

    public List<Schedule> findByFaculty(int facultyId) throws SQLException {
        List<Schedule> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT s.*, c.name as course_name, u.name as faculty_name " +
                        "FROM schedules s JOIN courses c ON s.course_id = c.course_id " +
                        "JOIN users u ON s.faculty_id = u.user_id WHERE s.faculty_id = ? ORDER BY s.time_slot");
        ps.setInt(1, facultyId);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            list.add(mapSchedule(rs));
        return list;
    }

    public List<Schedule> findByStudent(int studentId) throws SQLException {
        List<Schedule> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT s.*, c.name as course_name, u.name as faculty_name " +
                        "FROM schedules s JOIN courses c ON s.course_id = c.course_id " +
                        "JOIN users u ON s.faculty_id = u.user_id " +
                        "WHERE s.course_id IN (SELECT course_id FROM registrations WHERE student_id=? AND status='REGISTERED') "
                        +
                        "ORDER BY s.time_slot");
        ps.setInt(1, studentId);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            list.add(mapSchedule(rs));
        return list;
    }

    public boolean hasClash(int facultyId, String timeSlot, int excludeScheduleId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM schedules WHERE faculty_id=? AND time_slot=? AND schedule_id != ?");
        ps.setInt(1, facultyId);
        ps.setString(2, timeSlot);
        ps.setInt(3, excludeScheduleId);
        ResultSet rs = ps.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    public int insert(Schedule schedule) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO schedules (course_id, faculty_id, time_slot, room, status) VALUES (?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, schedule.getCourseId());
        ps.setInt(2, schedule.getFacultyId());
        ps.setString(3, schedule.getTimeSlot());
        ps.setString(4, schedule.getRoom());
        ps.setString(5, schedule.getStatus());
        ps.executeUpdate();
        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next()) {
            int id = keys.getInt(1);
            schedule.setScheduleId(id);
            return id;
        }
        return -1;
    }

    public void update(Schedule schedule) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE schedules SET course_id=?, faculty_id=?, time_slot=?, room=?, status=? WHERE schedule_id=?");
        ps.setInt(1, schedule.getCourseId());
        ps.setInt(2, schedule.getFacultyId());
        ps.setString(3, schedule.getTimeSlot());
        ps.setString(4, schedule.getRoom());
        ps.setString(5, schedule.getStatus());
        ps.setInt(6, schedule.getScheduleId());
        ps.executeUpdate();
    }

    public void delete(int scheduleId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM schedules WHERE schedule_id=?");
        ps.setInt(1, scheduleId);
        ps.executeUpdate();
    }

    private Schedule mapSchedule(ResultSet rs) throws SQLException {
        Schedule s = new Schedule(rs.getInt("schedule_id"), rs.getInt("course_id"),
                rs.getInt("faculty_id"), rs.getString("time_slot"), rs.getString("room"), rs.getString("status"));
        s.setCourseName(rs.getString("course_name"));
        s.setFacultyName(rs.getString("faculty_name"));
        return s;
    }
}
