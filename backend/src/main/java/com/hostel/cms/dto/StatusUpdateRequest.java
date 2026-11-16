package com.hostel.cms.dto;

public class StatusUpdateRequest {
    private String status; // Pending, In Progress, Resolved

    public StatusUpdateRequest() {}

    public StatusUpdateRequest(String status) {
        this.status = status;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
