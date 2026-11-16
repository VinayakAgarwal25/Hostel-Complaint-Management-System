package com.hostel.cms.model;

public class Staff {
    private Integer staffId;
    private String name;
    private Integer departmentId;
    private String departmentName;
    private String phone;

    public Staff() {}

    public Staff(Integer staffId, String name, Integer departmentId, String phone) {
        this.staffId = staffId;
        this.name = name;
        this.departmentId = departmentId;
        this.phone = phone;
    }

    public Integer getStaffId() { return staffId; }
    public void setStaffId(Integer staffId) { this.staffId = staffId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
