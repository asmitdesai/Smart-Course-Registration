package com.smartcourse.service;

import com.smartcourse.dao.ScheduleDAO;
import com.smartcourse.model.Schedule;
import java.sql.SQLException;
import java.util.List;

public class ScheduleService {
    private static ScheduleService instance;
    private final ScheduleDAO scheduleDAO = new ScheduleDAO();

    private ScheduleService() {
    }

    public static ScheduleService getInstance() {
        if (instance == null)
            instance = new ScheduleService();
        return instance;
    }

    public List<Schedule> getAllSchedules() throws SQLException {
        return scheduleDAO.findAll();
    }

    public List<Schedule> getSchedulesByFaculty(int facultyId) throws SQLException {
        return scheduleDAO.findByFaculty(facultyId);
    }

    public List<Schedule> getSchedulesByStudent(int studentId) throws SQLException {
        return scheduleDAO.findByStudent(studentId);
    }

    public boolean detectClash(int facultyId, String timeSlot, int excludeScheduleId) throws SQLException {
        return scheduleDAO.hasClash(facultyId, timeSlot, excludeScheduleId);
    }

    public void createSchedule(Schedule schedule) throws SQLException {
        scheduleDAO.insert(schedule);
    }

    public void updateSchedule(Schedule schedule) throws SQLException {
        scheduleDAO.update(schedule);
    }

    public void deleteSchedule(int scheduleId) throws SQLException {
        scheduleDAO.delete(scheduleId);
    }
}
