package com.hostel.cms.model;

import java.sql.Timestamp;

public class ComplaintFeedback {
    private Integer feedbackId;
    private Integer complaintId;
    private Integer rating;
    private String comments;
    private Timestamp feedbackDate;

    public ComplaintFeedback() {}

    public ComplaintFeedback(Integer feedbackId, Integer complaintId, Integer rating, String comments, Timestamp feedbackDate) {
        this.feedbackId = feedbackId;
        this.complaintId = complaintId;
        this.rating = rating;
        this.comments = comments;
        this.feedbackDate = feedbackDate;
    }

    public Integer getFeedbackId() { return feedbackId; }
    public void setFeedbackId(Integer feedbackId) { this.feedbackId = feedbackId; }

    public Integer getComplaintId() { return complaintId; }
    public void setComplaintId(Integer complaintId) { this.complaintId = complaintId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public Timestamp getFeedbackDate() { return feedbackDate; }
    public void setFeedbackDate(Timestamp feedbackDate) { this.feedbackDate = feedbackDate; }
}
