package com.hostel.cms;

import com.hostel.cms.dao.ComplaintDAO;
import com.hostel.cms.model.Complaint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ComplaintDAOTest {

    @Autowired
    private ComplaintDAO complaintDAO;

    @Test
    @DisplayName("Test 1: Find all complaints should return seeded records")
    void testFindAllComplaints() {
        List<Complaint> complaints = complaintDAO.findFiltered(null, null, null, null);
        assertNotNull(complaints);
        assertFalse(complaints.isEmpty(), "Complaints list should not be empty after seeding.");
        assertTrue(complaints.size() >= 10, "Should contain at least 10 sample complaints.");
    }

    @Test
    @DisplayName("Test 2: Find complaint by valid ID")
    void testFindById() {
        Optional<Complaint> complaint = complaintDAO.findById(1);
        assertTrue(complaint.isPresent(), "Complaint ID 1 should exist.");
        assertEquals(1, complaint.get().getComplaintId());
        assertNotNull(complaint.get().getDescription());
        assertNotNull(complaint.get().getUserName());
    }

    @Test
    @DisplayName("Test 3: Filter complaints by status 'Pending'")
    void testFilterByStatus() {
        List<Complaint> pending = complaintDAO.findFiltered("Pending", null, null, null);
        assertNotNull(pending);
        for (Complaint c : pending) {
            assertEquals("Pending", c.getStatus());
        }
    }

    @Test
    @DisplayName("Test 4: Create new complaint and verify persistence")
    void testCreateComplaint() {
        Complaint c = new Complaint();
        c.setUserId(1);
        c.setCategoryId(4);
        c.setDepartmentId(2);
        c.setDescription("JUnit Test Complaint: Speed test failure in room A-101");
        c.setPriority("High");

        Complaint created = complaintDAO.create(c);
        assertNotNull(created.getComplaintId(), "Created complaint must have an auto-generated ID.");
        assertEquals("Pending", created.getStatus());

        Optional<Complaint> fetched = complaintDAO.findById(created.getComplaintId());
        assertTrue(fetched.isPresent());
        assertEquals("High", fetched.get().getPriority());
    }

    @Test
    @DisplayName("Test 5: Assign staff to complaint")
    void testAssignStaff() {
        boolean assigned = complaintDAO.assignStaff(5, 10);
        assertTrue(assigned, "Assigning staff should return true.");

        Optional<Complaint> updated = complaintDAO.findById(5);
        assertTrue(updated.isPresent());
        assertEquals("In Progress", updated.get().getStatus(), "Complaint should move to In Progress when assigned.");
        assertEquals(10, updated.get().getAssignedStaffId());
    }
}
