package com.hostel.cms.model;

import java.sql.Timestamp;

public class Complaint {
    private Integer complaintId;
    private Integer userId;
    private Integer categoryId;
    private Integer departmentId;
    private String description;
    private String status; // Pending, In Progress, Resolved
    private String priority; // Low, Medium, High
    private Timestamp dateRegistered;
    private Timestamp dateResolved;

    // Joined fields for display
    private String userName;
    private String userEmail;
    private String roomNumber;
    private String hostelBlock;
    private String categoryName;
    private String departmentName;
    private Integer assignedStaffId;
    private String assignedStaffName;
    private Integer feedbackRating;
    private String feedbackComments;

    public Complaint() {}

    public Integer getComplaintId() { return complaintId; }
    public void setComplaintId(Integer complaintId) { this.complaintId = complaintId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Timestamp getDateRegistered() { return dateRegistered; }
    public void setDateRegistered(Timestamp dateRegistered) { this.dateRegistered = dateRegistered; }

    public Timestamp getDateResolved() { return dateResolved; }
    public void setDateResolved(Timestamp dateResolved) { this.dateResolved = dateResolved; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getHostelBlock() { return hostelBlock; }
    public void setHostelBlock(String hostelBlock) { this.hostelBlock = hostelBlock; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public Integer getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(Integer assignedStaffId) { this.assignedStaffId = assignedStaffId; }

    public String getAssignedStaffName() { return assignedStaffName; }
    public void setAssignedStaffName(String assignedStaffName) { this.assignedStaffName = assignedStaffName; }

    public Integer getFeedbackRating() { return feedbackRating; }
    public void setFeedbackRating(Integer feedbackRating) { this.feedbackRating = feedbackRating; }

    public String getFeedbackComments() { return feedbackComments; }
    public void setFeedbackComments(String feedbackComments) { this.feedbackComments = feedbackComments; }
}
