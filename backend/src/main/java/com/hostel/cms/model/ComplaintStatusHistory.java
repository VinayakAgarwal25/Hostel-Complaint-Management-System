package com.hostel.cms.model;

import java.sql.Timestamp;

public class ComplaintStatusHistory {
    private Integer historyId;
    private Integer complaintId;
    private String status;
    private Timestamp updatedOn;

    public ComplaintStatusHistory() {}

    public ComplaintStatusHistory(Integer historyId, Integer complaintId, String status, Timestamp updatedOn) {
        this.historyId = historyId;
        this.complaintId = complaintId;
        this.status = status;
        this.updatedOn = updatedOn;
    }

    public Integer getHistoryId() { return historyId; }
    public void setHistoryId(Integer historyId) { this.historyId = historyId; }

    public Integer getComplaintId() { return complaintId; }
    public void setComplaintId(Integer complaintId) { this.complaintId = complaintId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getUpdatedOn() { return updatedOn; }
    public void setUpdatedOn(Timestamp updatedOn) { this.updatedOn = updatedOn; }
}
