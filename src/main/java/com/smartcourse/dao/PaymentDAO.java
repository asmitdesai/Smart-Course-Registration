package com.smartcourse.dao;

import com.smartcourse.model.Payment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {
    private final Connection conn = DatabaseManager.getInstance().getConnection();

    public int insert(Payment payment) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO payments (student_id, course_id, amount, status) VALUES (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, payment.getStudentId());
        ps.setInt(2, payment.getCourseId());
        ps.setDouble(3, payment.getAmount());
        ps.setString(4, payment.getStatus());
        ps.executeUpdate();
        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next()) {
            int id = keys.getInt(1);
            payment.setPaymentId(id);
            return id;
        }
        return -1;
    }

    public void updateStatus(int paymentId, String status) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE payments SET status=?, payment_date=datetime('now') WHERE payment_id=?");
        ps.setString(1, status);
        ps.setInt(2, paymentId);
        ps.executeUpdate();
    }

    public List<Payment> findByStudent(int studentId) throws SQLException {
        List<Payment> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM payments WHERE student_id = ? ORDER BY payment_id DESC");
        ps.setInt(1, studentId);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            list.add(mapPayment(rs));
        return list;
    }

    private Payment mapPayment(ResultSet rs) throws SQLException {
        return new Payment(rs.getInt("payment_id"), rs.getInt("student_id"),
                rs.getInt("course_id"), rs.getDouble("amount"), rs.getString("status"));
    }
}
