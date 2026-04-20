package com.smartcourse.service;

import com.smartcourse.dao.UserDAO;
import com.smartcourse.model.User;
import java.sql.SQLException;

public class AuthService {
    private static AuthService instance;
    private final UserDAO userDAO = new UserDAO();
    private User currentUser;

    private AuthService() {
    }

    public static AuthService getInstance() {
        if (instance == null)
            instance = new AuthService();
        return instance;
    }

    public User login(String email, String password) throws SQLException {
        User user = userDAO.findByEmail(email);
        if (user != null && org.mindrot.jbcrypt.BCrypt.checkpw(password, user.getPassword())) {
            currentUser = user;
            return user;
        }
        return null;
    }

    public void register(User user) throws SQLException {
        if (userDAO.existsByEmail(user.getEmail())) {
            throw new SQLException("A user with this email already exists.");
        }
        // Hash password before saving
        String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(user.getPassword(), org.mindrot.jbcrypt.BCrypt.gensalt());
        user.setPassword(hashedPassword);
        userDAO.insert(user);
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
