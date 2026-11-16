package com.hostel.cms.dto;

public class AssignStaffRequest {
    private Integer staffId;

    public AssignStaffRequest() {}

    public AssignStaffRequest(Integer staffId) {
        this.staffId = staffId;
    }

    public Integer getStaffId() { return staffId; }
    public void setStaffId(Integer staffId) { this.staffId = staffId; }
}
