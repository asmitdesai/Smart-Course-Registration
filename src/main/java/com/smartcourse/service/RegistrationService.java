package com.smartcourse.service;

import com.smartcourse.dao.*;
import com.smartcourse.model.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class RegistrationService {
    private static RegistrationService instance;
    private final RegistrationDAO registrationDAO = new RegistrationDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final GradeDAO gradeDAO = new GradeDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    private RegistrationService() {
    }

    public static RegistrationService getInstance() {
        if (instance == null)
            instance = new RegistrationService();
        return instance;
    }

    public enum RegistrationResult {
        SUCCESS, SEAT_FULL, ALREADY_REGISTERED, PREREQUISITE_NOT_MET, PAYMENT_FAILED
    }

    public RegistrationResult register(int studentId, int courseId, CourseService courseService) throws SQLException {
        if (courseService.alreadyRegistered(studentId, courseId))
            return RegistrationResult.ALREADY_REGISTERED;
        if (!courseService.checkSeatAvailability(courseId))
            return RegistrationResult.SEAT_FULL;
        if (!courseService.checkPrerequisites(studentId, courseId))
            return RegistrationResult.PREREQUISITE_NOT_MET;

        Registration reg = new Registration();
        reg.setStudentId(studentId);
        reg.setCourseId(courseId);
        reg.setStatus("PAYMENT_PENDING");
        reg.setRegistrationDate(LocalDateTime.now());
        registrationDAO.insert(reg);
        return RegistrationResult.SUCCESS;
    }

    public boolean processPayment(int studentId, int courseId, int regId) throws SQLException {
        Course course = courseDAO.findById(courseId);
        double amount = course != null ? course.getCredits() * 150.0 : 450.0;
        Payment payment = new Payment(0, studentId, courseId, amount, "PENDING");
        paymentDAO.insert(payment);

        payment.processPayment();
        paymentDAO.updateStatus(payment.getPaymentId(), payment.getStatus());

        if ("SUCCESS".equals(payment.getStatus())) {
            // Attempt to secure the seat
            if (courseDAO.decrementSeats(courseId)) {
                registrationDAO.updateStatus(regId, "REGISTERED");
                // Initialize grade and attendance records
                Grade grade = new Grade(0, studentId, courseId, 0);
                gradeDAO.upsert(grade);
                Attendance att = new Attendance(0, studentId, courseId, 0, 0);
                attendanceDAO.upsert(att);
                return true;
            } else {
                // Payment was successful but seat is gone (race condition)
                registrationDAO.updateStatus(regId, "SEAT_UNAVAILABLE");
                // In a real system, you'd trigger a refund here
                return false;
            }
        } else {
            registrationDAO.updateStatus(regId, "PAYMENT_FAILED");
            return false;
        }
    }

    public void dropCourse(int regId, int courseId) throws SQLException {
        registrationDAO.updateStatus(regId, "DROPPED");
        courseDAO.incrementSeats(courseId);
    }

    public List<Registration> getStudentRegistrations(int studentId) throws SQLException {
        return registrationDAO.findByStudent(studentId);
    }

    public List<Registration> getAllRegistrations() throws SQLException {
        return registrationDAO.findAll();
    }
}
