package com.hostel.cms.controller;

import com.hostel.cms.dao.StaffDAO;
import com.hostel.cms.dao.UserDAO;
import com.hostel.cms.model.Staff;
import com.hostel.cms.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserDAO userDAO;
    private final StaffDAO staffDAO;

    public AuthController(UserDAO userDAO, StaffDAO staffDAO) {
        this.userDAO = userDAO;
        this.staffDAO = staffDAO;
    }

    public static class LoginRequest {
        private String email;
        private Integer userId;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Optional<User> userOpt = Optional.empty();
        if (req.getUserId() != null) {
            userOpt = userDAO.findById(req.getUserId());
        } else if (req.getEmail() != null && !req.getEmail().trim().isEmpty()) {
            userOpt = userDAO.findByEmail(req.getEmail().trim());
        }

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Map<String, Object> res = new HashMap<>();
            res.put("userId", user.getUserId());
            res.put("name", user.getName());
            res.put("email", user.getEmail());
            res.put("role", user.getRole());
            res.put("roomNumber", user.getRoomNumber());
            res.put("hostelBlock", user.getHostelBlock());
            return ResponseEntity.ok(res);
        }

        // Check if staff email
        if (req.getEmail() != null) {
            var allStaff = staffDAO.findAll();
            for (Staff s : allStaff) {
                if (s.getName().toLowerCase().contains(req.getEmail().toLowerCase())) {
                    Map<String, Object> res = new HashMap<>();
                    res.put("staffId", s.getStaffId());
                    res.put("name", s.getName());
                    res.put("role", "STAFF");
                    res.put("departmentId", s.getDepartmentId());
                    res.put("departmentName", s.getDepartmentName());
                    return ResponseEntity.ok(res);
                }
            }
        }

        Map<String, String> err = new HashMap<>();
        err.put("error", "Invalid credentials or user not found.");
        return ResponseEntity.status(401).body(err);
    }
}
