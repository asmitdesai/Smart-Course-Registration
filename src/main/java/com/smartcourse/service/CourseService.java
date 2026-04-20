package com.smartcourse.service;

import com.smartcourse.dao.CourseDAO;
import com.smartcourse.dao.RegistrationDAO;
import com.smartcourse.model.Course;
import java.sql.SQLException;
import java.util.List;

public class CourseService {
    private static CourseService instance;
    private final CourseDAO courseDAO = new CourseDAO();
    private final RegistrationDAO registrationDAO = new RegistrationDAO();

    private CourseService() {
    }

    public static CourseService getInstance() {
        if (instance == null)
            instance = new CourseService();
        return instance;
    }

    public List<Course> getAllCourses() throws SQLException {
        return courseDAO.findAll();
    }

    public Course getCourse(int courseId) throws SQLException {
        return courseDAO.findById(courseId);
    }

    public boolean checkSeatAvailability(int courseId) throws SQLException {
        Course c = courseDAO.findById(courseId);
        return c != null && c.hasSeatAvailable();
    }

    public boolean checkPrerequisites(int studentId, int courseId) throws SQLException {
        List<Integer> prereqs = courseDAO.findPrerequisiteIds(courseId);
        for (int prereqId : prereqs) {
            if (!registrationDAO.hasCompletedCourse(studentId, prereqId))
                return false;
        }
        return true;
    }

    public boolean alreadyRegistered(int studentId, int courseId) throws SQLException {
        return registrationDAO.existsActiveRegistration(studentId, courseId);
    }

    public void createCourse(Course course) throws SQLException {
        courseDAO.insert(course);
    }

    public void updateCourse(Course course) throws SQLException {
        courseDAO.update(course);
    }

    public void deleteCourse(int courseId) throws SQLException {
        courseDAO.delete(courseId);
    }

    public CourseDAO getCourseDAO() {
        return courseDAO;
    }
}
