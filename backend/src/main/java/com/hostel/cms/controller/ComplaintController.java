package com.hostel.cms.controller;

import com.hostel.cms.dto.AssignStaffRequest;
import com.hostel.cms.dto.ComplaintCreateRequest;
import com.hostel.cms.dto.FeedbackRequest;
import com.hostel.cms.dto.StatusUpdateRequest;
import com.hostel.cms.model.Complaint;
import com.hostel.cms.model.ComplaintFeedback;
import com.hostel.cms.model.ComplaintStatusHistory;
import com.hostel.cms.service.ComplaintService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping
    public ResponseEntity<List<Complaint>> getComplaints(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Integer userId
    ) {
        List<Complaint> list = complaintService.getComplaints(status, departmentId, priority, userId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getComplaintById(@PathVariable int id) {
        return complaintService.getComplaintById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createComplaint(@RequestBody ComplaintCreateRequest request) {
        try {
            Complaint created = complaintService.createComplaint(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> err = new HashMap<>();
            err.put("error", "Failed to register complaint: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable int id, @RequestBody StatusUpdateRequest request) {
        try {
            boolean updated = complaintService.updateStatus(id, request.getStatus());
            Map<String, Object> res = new HashMap<>();
            res.put("success", updated);
            res.put("message", "Status updated successfully.");
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<?> assignStaff(@PathVariable int id, @RequestBody AssignStaffRequest request) {
        try {
            boolean assigned = complaintService.assignStaff(id, request.getStaffId());
            Map<String, Object> res = new HashMap<>();
            res.put("success", assigned);
            res.put("message", "Staff assigned successfully.");
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @PostMapping("/{id}/feedback")
    public ResponseEntity<?> submitFeedback(@PathVariable int id, @RequestBody FeedbackRequest request) {
        try {
            ComplaintFeedback fb = complaintService.submitFeedback(id, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(fb);
        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<ComplaintStatusHistory>> getStatusHistory(@PathVariable int id) {
        return ResponseEntity.ok(complaintService.getStatusHistory(id));
    }
}
