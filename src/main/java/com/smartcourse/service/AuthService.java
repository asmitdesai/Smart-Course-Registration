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
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return user;
        }
        return null;
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
