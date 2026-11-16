package com.hostel.cms.service;

import com.hostel.cms.dao.UserDAO;
import com.hostel.cms.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public Optional<User> getUserById(int userId) {
        return userDAO.findById(userId);
    }

    public Optional<User> getUserByEmail(String email) {
        return userDAO.findByEmail(email);
    }
}
