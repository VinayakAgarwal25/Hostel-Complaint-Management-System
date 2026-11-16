package com.hostel.cms;

import com.hostel.cms.dto.ComplaintCreateRequest;
import com.hostel.cms.dto.FeedbackRequest;
import com.hostel.cms.model.Complaint;
import com.hostel.cms.model.ComplaintFeedback;
import com.hostel.cms.service.ComplaintService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ComplaintServiceTest {

    @Autowired
    private ComplaintService complaintService;

    @Test
    @DisplayName("Service Test 1: Create complaint with missing fields throws exception")
    void testCreateComplaintValidation() {
        ComplaintCreateRequest req = new ComplaintCreateRequest();
        req.setDescription(""); // empty description
        assertThrows(IllegalArgumentException.class, () -> complaintService.createComplaint(req));
    }

    @Test
    @DisplayName("Service Test 2: Valid complaint creation flows to DB")
    void testCreateComplaintSuccess() {
        ComplaintCreateRequest req = new ComplaintCreateRequest();
        req.setUserId(2);
        req.setCategoryId(2);
        req.setDepartmentId(8);
        req.setDescription("Service Test: Tap dripping in washroom second floor");
        req.setPriority("Medium");

        Complaint created = complaintService.createComplaint(req);
        assertNotNull(created);
        assertEquals("Pending", created.getStatus());
        assertEquals("Ananya Iyer", created.getUserName());
    }

    @Test
    @DisplayName("Service Test 3: Feedback validation - rating must be between 1 and 5")
    void testFeedbackValidation() {
        FeedbackRequest invalidFeedback = new FeedbackRequest(7, "Out of bounds rating");
        assertThrows(IllegalArgumentException.class, () ->
                complaintService.submitFeedback(1, invalidFeedback)
        );
    }

    @Test
    @DisplayName("Service Test 4: Cannot submit feedback on Pending complaints")
    void testFeedbackOnPendingComplaint() {
        FeedbackRequest req = new FeedbackRequest(5, "Great work!");
        // Complaint ID 9 is Pending in sample data
        assertThrows(IllegalStateException.class, () ->
                complaintService.submitFeedback(9, req)
        );
    }

    @Test
    @DisplayName("Service Test 5: Status update to Resolved sets dateResolved and history")
    void testUpdateStatusToResolved() {
        boolean updated = complaintService.updateStatus(3, "Resolved");
        assertTrue(updated);

        var history = complaintService.getStatusHistory(3);
        assertFalse(history.isEmpty());
        assertTrue(history.stream().anyMatch(h -> "Resolved".equals(h.getStatus())));
    }
}
