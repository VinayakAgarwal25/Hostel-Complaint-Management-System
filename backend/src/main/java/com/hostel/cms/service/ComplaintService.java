package com.hostel.cms.service;

import com.hostel.cms.dao.ComplaintDAO;
import com.hostel.cms.dao.FeedbackDAO;
import com.hostel.cms.dto.ComplaintCreateRequest;
import com.hostel.cms.dto.FeedbackRequest;
import com.hostel.cms.model.Complaint;
import com.hostel.cms.model.ComplaintFeedback;
import com.hostel.cms.model.ComplaintStatusHistory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class ComplaintService {

    private final ComplaintDAO complaintDAO;
    private final FeedbackDAO feedbackDAO;

    public ComplaintService(ComplaintDAO complaintDAO, FeedbackDAO feedbackDAO) {
        this.complaintDAO = complaintDAO;
        this.feedbackDAO = feedbackDAO;
    }

    public List<Complaint> getComplaints(String status, Integer departmentId, String priority, Integer userId) {
        return complaintDAO.findFiltered(status, departmentId, priority, userId);
    }

    public Optional<Complaint> getComplaintById(int complaintId) {
        return complaintDAO.findById(complaintId);
    }

    @Transactional
    public Complaint createComplaint(ComplaintCreateRequest req) {
        if (req.getUserId() == null || req.getCategoryId() == null || req.getDepartmentId() == null) {
            throw new IllegalArgumentException("User ID, Category ID, and Department ID are required.");
        }
        if (req.getDescription() == null || req.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty.");
        }

        Complaint complaint = new Complaint();
        complaint.setUserId(req.getUserId());
        complaint.setCategoryId(req.getCategoryId());
        complaint.setDepartmentId(req.getDepartmentId());
        complaint.setDescription(req.getDescription().trim());
        complaint.setPriority(req.getPriority() != null ? req.getPriority() : "Medium");

        Complaint created = complaintDAO.create(complaint);
        return complaintDAO.findById(created.getComplaintId()).orElse(created);
    }

    @Transactional
    public boolean updateStatus(int complaintId, String newStatus) {
        Complaint complaint = complaintDAO.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint with ID " + complaintId + " not found."));

        Timestamp dateResolved = null;
        if ("Resolved".equalsIgnoreCase(newStatus)) {
            dateResolved = new Timestamp(System.currentTimeMillis());
        }

        return complaintDAO.updateStatus(complaintId, newStatus, dateResolved);
    }

    @Transactional
    public boolean assignStaff(int complaintId, int staffId) {
        complaintDAO.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint with ID " + complaintId + " not found."));
        return complaintDAO.assignStaff(complaintId, staffId);
    }

    @Transactional
    public ComplaintFeedback submitFeedback(int complaintId, FeedbackRequest req) {
        Complaint complaint = complaintDAO.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint with ID " + complaintId + " not found."));

        if (!"Resolved".equalsIgnoreCase(complaint.getStatus())) {
            throw new IllegalStateException("Feedback can only be submitted for Resolved complaints.");
        }

        if (req.getRating() == null || req.getRating() < 1 || req.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be an integer between 1 and 5.");
        }

        ComplaintFeedback feedback = new ComplaintFeedback();
        feedback.setComplaintId(complaintId);
        feedback.setRating(req.getRating());
        feedback.setComments(req.getComments() != null ? req.getComments().trim() : "");

        return feedbackDAO.save(feedback);
    }

    public List<ComplaintStatusHistory> getStatusHistory(int complaintId) {
        return complaintDAO.getStatusHistory(complaintId);
    }
}
