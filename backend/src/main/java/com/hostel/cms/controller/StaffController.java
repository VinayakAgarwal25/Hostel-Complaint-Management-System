package com.hostel.cms.controller;

import com.hostel.cms.dao.StaffDAO;
import com.hostel.cms.dto.StaffPerformanceDTO;
import com.hostel.cms.model.Staff;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private final StaffDAO staffDAO;

    public StaffController(StaffDAO staffDAO) {
        this.staffDAO = staffDAO;
    }

    @GetMapping
    public ResponseEntity<List<Staff>> getStaff(@RequestParam(required = false) Integer departmentId) {
        if (departmentId != null) {
            return ResponseEntity.ok(staffDAO.findByDepartmentId(departmentId));
        }
        return ResponseEntity.ok(staffDAO.findAll());
    }

    @GetMapping("/performance")
    public ResponseEntity<List<StaffPerformanceDTO>> getStaffPerformance() {
        return ResponseEntity.ok(staffDAO.getStaffPerformance());
    }

    @PostMapping
    public ResponseEntity<?> addStaff(@RequestBody Staff staff) {
        if (staff.getName() == null || staff.getName().trim().isEmpty()) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Staff name cannot be empty.");
            return ResponseEntity.badRequest().body(err);
        }
        if (staff.getDepartmentId() == null) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Department ID is required.");
            return ResponseEntity.badRequest().body(err);
        }

        try {
            Staff saved = staffDAO.save(staff);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Failed to add staff: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }
}
