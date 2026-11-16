package com.hostel.cms.dto;

import java.util.Map;

public class DashboardStatsDTO {
    private int totalComplaints;
    private int pendingComplaints;
    private int inProgressComplaints;
    private int resolvedComplaints;
    private double resolutionRate;
    private double averageRating;
    private Map<String, Integer> complaintsByDepartment;
    private Map<String, Integer> complaintsByStatus;

    public DashboardStatsDTO() {}

    public int getTotalComplaints() { return totalComplaints; }
    public void setTotalComplaints(int totalComplaints) { this.totalComplaints = totalComplaints; }

    public int getPendingComplaints() { return pendingComplaints; }
    public void setPendingComplaints(int pendingComplaints) { this.pendingComplaints = pendingComplaints; }

    public int getInProgressComplaints() { return inProgressComplaints; }
    public void setInProgressComplaints(int inProgressComplaints) { this.inProgressComplaints = inProgressComplaints; }

    public int getResolvedComplaints() { return resolvedComplaints; }
    public void setResolvedComplaints(int resolvedComplaints) { this.resolvedComplaints = resolvedComplaints; }

    public double getResolutionRate() { return resolutionRate; }
    public void setResolutionRate(double resolutionRate) { this.resolutionRate = resolutionRate; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public Map<String, Integer> getComplaintsByDepartment() { return complaintsByDepartment; }
    public void setComplaintsByDepartment(Map<String, Integer> complaintsByDepartment) { this.complaintsByDepartment = complaintsByDepartment; }

    public Map<String, Integer> getComplaintsByStatus() { return complaintsByStatus; }
    public void setComplaintsByStatus(Map<String, Integer> complaintsByStatus) { this.complaintsByStatus = complaintsByStatus; }
}
