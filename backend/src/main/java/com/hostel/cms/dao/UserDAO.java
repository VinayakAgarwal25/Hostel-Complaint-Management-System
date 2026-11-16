package com.hostel.cms.dao;

import com.hostel.cms.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDAO {
    List<User> findAll();
    Optional<User> findById(int userId);
    Optional<User> findByEmail(String email);
    User save(User user);
}
