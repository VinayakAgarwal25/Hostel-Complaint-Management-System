package com.hostel.cms.model;

import java.sql.Timestamp;

public class ComplaintAssignment {
    private Integer assignmentId;
    private Integer complaintId;
    private Integer staffId;
    private Timestamp assignedDate;

    public ComplaintAssignment() {}

    public ComplaintAssignment(Integer assignmentId, Integer complaintId, Integer staffId, Timestamp assignedDate) {
        this.assignmentId = assignmentId;
        this.complaintId = complaintId;
        this.staffId = staffId;
        this.assignedDate = assignedDate;
    }

    public Integer getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Integer assignmentId) { this.assignmentId = assignmentId; }

    public Integer getComplaintId() { return complaintId; }
    public void setComplaintId(Integer complaintId) { this.complaintId = complaintId; }

    public Integer getStaffId() { return staffId; }
    public void setStaffId(Integer staffId) { this.staffId = staffId; }

    public Timestamp getAssignedDate() { return assignedDate; }
    public void setAssignedDate(Timestamp assignedDate) { this.assignedDate = assignedDate; }
}
